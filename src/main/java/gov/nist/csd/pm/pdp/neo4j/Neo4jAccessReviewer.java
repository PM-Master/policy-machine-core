package gov.nist.csd.pm.pdp.neo4j;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.neo4j.Neo4jConnection;
import gov.nist.csd.pm.pap.neo4j.Neo4jGraphStore;
import gov.nist.csd.pm.pap.neo4j.Neo4jProhibitionsStore;
import gov.nist.csd.pm.pdp.memory.AccessRightResolver;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.AccessRightSet;
import gov.nist.csd.pm.policy.model.access.UserContext;
import gov.nist.csd.pm.policy.model.audit.EdgePath;
import gov.nist.csd.pm.policy.model.audit.Explain;
import gov.nist.csd.pm.policy.model.audit.PolicyClass;
import gov.nist.csd.pm.policy.model.graph.dag.TargetDagResult;
import gov.nist.csd.pm.policy.model.graph.dag.UserDagResult;
import gov.nist.csd.pm.policy.model.graph.dag.propagator.Propagator;
import gov.nist.csd.pm.policy.model.graph.dag.visitor.Visitor;
import gov.nist.csd.pm.policy.model.graph.dag.walker.dfs.DepthFirstGraphWalker;
import gov.nist.csd.pm.policy.model.graph.relationships.Association;
import gov.nist.csd.pm.policy.model.prohibition.ContainerCondition;
import gov.nist.csd.pm.policy.model.prohibition.Prohibition;
import gov.nist.csd.pm.policy.review.AccessReview;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;

import java.util.*;

import static gov.nist.csd.pm.pap.neo4j.Neo4jGraphStore.*;
import static gov.nist.csd.pm.pap.neo4j.Neo4jProhibitionsStore.*;
import static gov.nist.csd.pm.pdp.memory.AccessRightResolver.*;
import static gov.nist.csd.pm.pdp.memory.MemoryAccessReviewer.resolvePaths;
import static gov.nist.csd.pm.policy.model.graph.nodes.NodeType.U;
import static gov.nist.csd.pm.policy.model.graph.nodes.Properties.NO_PROPERTIES;

public class Neo4jAccessReviewer implements AccessReview {

    private final Neo4jConnection neo4j;
    private final Neo4jGraphStore neo4jGraphStore;

    public Neo4jAccessReviewer(GraphDatabaseService graph) {
        this.neo4j = new Neo4jConnection(graph);
        this.neo4jGraphStore = new Neo4jGraphStore(neo4j);
    }

    @Override
    public AccessRightSet computePrivileges(UserContext userCtx, String target) throws PMException {
        AccessRightSet accessRights = new AccessRightSet();

        // traverse the user side of the graph to get the associations
        UserDagResult userDagResult = processUserDAG(userCtx);
        if (userDagResult.borderTargets().isEmpty()) {
            return accessRights;
        }

        // traverse the target side of the graph to get ars per policy class
        TargetDagResult targetDagResult = processTargetDAG(target, userDagResult);

        // resolve the permissions
        return AccessRightResolver.resolvePrivileges(
                userDagResult,
                targetDagResult,
                target,
                neo4jGraphStore.getResourceAccessRights()
        );
    }

    @Override
    public AccessRightSet computeDeniedPrivileges(UserContext userCtx, String target) throws PMException {
        AccessRightSet accessRights = new AccessRightSet();

        // traverse the user side of the graph to get the associations
        UserDagResult userDagResult = processUserDAG(userCtx);
        if (userDagResult.borderTargets().isEmpty()) {
            return accessRights;
        }

        // traverse the target side of the graph to get permissions per policy class
        TargetDagResult targetDagResult = processTargetDAG(target, userDagResult);

        // resolve the permissions
        return resolveDeniedAccessRights(userDagResult, targetDagResult, target);
    }

    @Override
    public Map<String, AccessRightSet> computePolicyClassAccessRights(UserContext userCtx, String target)
            throws PMException {
        // traverse the user side of the graph to get the associations
        UserDagResult userDagResult = processUserDAG(userCtx);
        if (userDagResult.borderTargets().isEmpty()) {
            return new HashMap<>();
        }

        // traverse the target side of the graph to get permissions per policy class
        TargetDagResult targetDagResult = processTargetDAG(target, userDagResult);

        return targetDagResult.pcSet();
    }

    @Override
    public Map<String, AccessRightSet> buildCapabilityList(UserContext userCtx) throws PMException {
        Map<String, AccessRightSet> results = new HashMap<>();

        //get border nodes.  Can be OA or UA.  Return empty set if no attrs are reachable
        UserDagResult userDagResult = processUserDAG(userCtx);
        if (userDagResult.borderTargets().isEmpty()) {
            return results;
        }

        for(String borderTarget : userDagResult.borderTargets().keySet()) {
            // compute permissions on the border attr
            getAndStorePrivileges(results, userDagResult, borderTarget);

            // compute decisions for the subgraph of the border attr
            Set<String> descendants = getDescendants(borderTarget);
            for (String descendant : descendants) {
                if (results.containsKey(descendant)) {
                    continue;
                }

                getAndStorePrivileges(results, userDagResult, descendant);
            }
        }

        return results;
    }

    @Override
    public Map<String, AccessRightSet> buildACL(String target) throws PMException {
        Map<String, AccessRightSet> acl = new HashMap<>();
        List<String> search = neo4jGraphStore.search(U, NO_PROPERTIES);
        for (String user : search) {
            AccessRightSet list = this.computePrivileges(new UserContext(user), target);
            acl.put(user, list);
        }

        return acl;
    }

    @Override
    public Map<String, AccessRightSet> findBorderAttributes(String user) throws PMException {
        return processUserDAG(new UserContext(user))
                .borderTargets();
    }

    @Override
    public Map<String, AccessRightSet> computeSubgraphPrivileges(UserContext userCtx, String root) throws PMException {
        Map<String, AccessRightSet> results = new HashMap<>();

        UserDagResult userDagResult = processUserDAG(userCtx);
        if (userDagResult.borderTargets().isEmpty()) {
            return results;
        }

        Set<String> descendants = getDescendants(root);
        for (String descendant : descendants) {
            if (results.containsKey(descendant)) {
                continue;
            }

            getAndStorePrivileges(results, userDagResult, descendant);
        }

        return results;
    }

    @Override
    public Explain explain(UserContext userCtx, String target) throws PMException {
        gov.nist.csd.pm.policy.model.graph.nodes.Node userNode = neo4jGraphStore.getNode(userCtx.getUser());
        gov.nist.csd.pm.policy.model.graph.nodes.Node targetNode = neo4jGraphStore.getNode(target);

        List<EdgePath> userPaths = explainDfs(userNode.getName());
        List<EdgePath> targetPaths = explainDfs(targetNode.getName());

        Map<String, PolicyClass> resolvedPaths = resolvePaths(neo4jGraphStore, userPaths, targetPaths, target);

        UserDagResult userDagResult = processUserDAG(userCtx);
        TargetDagResult targetDagResult = processTargetDAG(target, userDagResult);

        AccessRightSet priv = resolvePrivileges(userDagResult, targetDagResult, target, neo4jGraphStore.getResourceAccessRights());
        AccessRightSet deniedPriv = resolveDeniedAccessRights(userDagResult, targetDagResult, target);
        List<Prohibition> prohibitions = computeSatisfiedProhibitions(userDagResult, targetDagResult, target);

        return new Explain(priv, resolvedPaths, deniedPriv, prohibitions);
    }

    @Override
    public Set<String> buildPOS(UserContext userCtx) throws PMException {
        // Prepare the hashset to return.
        HashSet<String> hsOa = new HashSet<>();

        // Call find_border_oa_priv(u). The result is a Hashtable
        // htoa = {oa -> {op -> pcset}}:
        Hashtable<String, Hashtable<String, Set<String>>> htOa = findBorderOaPrivRestrictedInternal(userCtx);

        // For each returned oa (key in htOa)
        for (Enumeration<String> oas = htOa.keys(); oas.hasMoreElements(); ) {
            String oa = oas.nextElement();

            // Compute oa's required PCs by calling find_pc_set(oa).
            HashSet<String> hsReqPcs = inMemFindPcSet(oa);
            // Extract oa's label.
            Hashtable<String, Set<String>> htOaLabel = htOa.get(oa);

            // Walk through the op -> pcset of the oa's label.
            // For each operation/access right
            for (Enumeration ops = htOaLabel.keys(); ops.hasMoreElements(); ) {
                String sOp = (String)ops.nextElement();
                // Extract the pcset corresponding to this operation/access right.
                Set<String> hsActualPcs = htOaLabel.get(sOp);
                // if the set of required PCs is a subset of the actual pcset,
                // then user u has some privileges on the current oa node.
                if (hsActualPcs.containsAll(hsReqPcs)) {
                    hsOa.add(oa);
                    break;
                }
            }
        }

        return new HashSet<>(hsOa);
    }

    @Override
    public List<String> computeAccessibleChildren(UserContext userCtx, String root) throws PMException {
        List<String> children = new ArrayList<>(neo4jGraphStore.getChildren(root));
        children.removeIf(child -> {
            try {
                return computePrivileges(userCtx, child).isEmpty();
            } catch (PMException e) {
                e.printStackTrace();
                return true;
            }
        });

        return children;
    }

    @Override
    public List<String> computeAccessibleParents(UserContext userCtx, String root) throws PMException {
        List<String> parents = new ArrayList<>(neo4jGraphStore.getParents(root));
        parents.removeIf(parent -> {
            try {
                return computePrivileges(userCtx, parent).isEmpty();
            } catch (PMException e) {
                e.printStackTrace();
                return true;
            }
        });

        return parents;
    }

    private Set<String> getDescendants(String name) throws PMException {
        return neo4j.runTx(tx -> {
            Node node = tx.findNode(NODE_LABEL, NAME_PROPERTY, name);
            Traverser traverse = tx.traversalDescription()
                                   .breadthFirst()
                                   .relationships(ASSIGNMENT_RELATIONSHIP_TYPE, Direction.INCOMING)
                                   .traverse(node);

            Set<String> descendants = new HashSet<>();
            for (Node n : traverse.nodes()) {
                if (n.equals(node)) {
                    continue;
                }

                descendants.add(String.valueOf(n.getProperty(NAME_PROPERTY)));
            }

            return descendants;
        });
    }

    private void getAndStorePrivileges(Map<String, AccessRightSet> arsetMap, UserDagResult userDagResult, String target) throws PMException {
        TargetDagResult targetCtx = processTargetDAG(target, userDagResult);
        AccessRightSet privileges = resolvePrivileges(userDagResult, targetCtx, target, neo4jGraphStore.getResourceAccessRights());
        arsetMap.put(target, privileges);
    }

    private UserDagResult processUserDAG(UserContext userCtx) throws PMException {
        return neo4j.runTx(tx -> {
            Node uNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, userCtx.getUser());

            // traverse paths starting at the u node
            Traverser traverse = tx.traversalDescription()
                                   .breadthFirst()
                                   .relationships(ASSIGNMENT_RELATIONSHIP_TYPE, Direction.OUTGOING)
                                   .relationships(ASSOCIATION_RELATIONSHIP_TYPE, Direction.OUTGOING)
                                   .relationships(PROHIBITION_SUBJECT_REL_TYPE, Direction.OUTGOING)
                                   .uniqueness(Uniqueness.NONE)
                                   .evaluator(path -> {
                                       // check if last rel is an association
                                       Relationship last = path.lastRelationship();
                                       if (last == null) {
                                           return Evaluation.EXCLUDE_AND_CONTINUE;
                                       } else if (last.isType(ASSOCIATION_RELATIONSHIP_TYPE)) {
                                           return Evaluation.INCLUDE_AND_CONTINUE;
                                       }

                                       // check for reach prohibitions
                                       Node endNode = path.endNode();
                                       if (endNode.hasLabel(PROHIBITION_LABEL)) {
                                           return Evaluation.INCLUDE_AND_CONTINUE;
                                       }

                                       return Evaluation.EXCLUDE_AND_CONTINUE;
                                   })
                                   .traverse(uNode);

            Map<String, AccessRightSet> assocs = new HashMap<>();
            Set<Prohibition> prohibitions = new HashSet<>();
            Set<String> prohibitionTargets = new HashSet<>();

            for (org.neo4j.graphdb.Path path : traverse) {
                Node endNode = path.endNode();
                String endNodeName = String.valueOf(endNode.getProperty(NAME_PROPERTY));

                if (endNode.hasLabel(PROHIBITION_LABEL)) {
                    Prohibition prohibition = Neo4jProhibitionsStore.getProhibitionFromNode(endNode);
                    prohibitions.add(prohibition);

                    List<ContainerCondition> containers = prohibition.getContainers();
                    for (ContainerCondition cc : containers) {
                        prohibitionTargets.add(cc.getName());
                    }
                } else {
                    Relationship last = path.lastRelationship();
                    String[] arset = (String[])last.getProperty(ARSET_PROPERTY);

                    AccessRightSet ars = assocs.getOrDefault(endNodeName, new AccessRightSet());
                    ars.addAll(List.of(arset));
                    assocs.put(endNodeName, ars);
                }
            }

            // process prohibition
            Node processNode = tx.findNode(PROCESS_LABEL, NAME_PROPERTY, userCtx.getProcess());
            if (processNode != null) {
                ResourceIterator<Relationship> rels = processNode.getRelationships(
                        Direction.OUTGOING,
                        PROHIBITION_SUBJECT_REL_TYPE
                ).iterator();

                while(rels.hasNext()) {
                    Relationship next = rels.next();
                    Node proNode = next.getEndNode();

                    Prohibition prohibition = Neo4jProhibitionsStore.getProhibitionFromNode(proNode);
                    prohibitions.add(prohibition);

                    List<ContainerCondition> containers = prohibition.getContainers();
                    for (ContainerCondition cc : containers) {
                        prohibitionTargets.add(cc.getName());
                    }
                }
            }

            return new UserDagResult(
                    assocs,
                    prohibitions,
                    prohibitionTargets
            );
        });
    }

    private TargetDagResult processTargetDAG(String target, UserDagResult userDagResult) throws PMException {
        return neo4j.runTx(tx -> {
            Node oNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, target);

            Map<String, AccessRightSet> pcMap = new HashMap<>();
            Set<String> reachedContainers = new HashSet<>();
            Traverser traverse = tx.traversalDescription()
                         .breadthFirst()
                         .relationships(ASSIGNMENT_RELATIONSHIP_TYPE, Direction.OUTGOING)
                         .relationships(PROHIBITION_CONTAINER_REL_TYPE, Direction.OUTGOING)
                         .uniqueness(Uniqueness.NONE)
                         .evaluator(path -> {
                             // check if last node is a Prohibition or PC
                             Node endNode = path.endNode();
                             if (endNode.hasLabel(PROHIBITION_LABEL) || endNode.hasLabel(PC_LABEL)) {
                                 return Evaluation.INCLUDE_AND_CONTINUE;
                             }

                             return Evaluation.EXCLUDE_AND_CONTINUE;
                         })
                         .traverse(oNode);
            for (org.neo4j.graphdb.Path path : traverse) {
                Node endNode = path.endNode();
                String endNodeName = String.valueOf(endNode.getProperty(NAME_PROPERTY));

                if (endNode.hasLabel(PROHIBITION_LABEL)) {
                    Relationship proContRel = path.lastRelationship();
                    Node contNode = proContRel.getStartNode();
                    reachedContainers.add(String.valueOf(contNode.getProperty(NAME_PROPERTY)));
                } else {
                    AccessRightSet ars = pcMap.putIfAbsent(endNodeName, new AccessRightSet());
                    if (ars == null) {
                        ars = new AccessRightSet();
                    }

                    for (Node node : path.nodes()) {
                        String name = String.valueOf(node.getProperty(NAME_PROPERTY));
                        if (!userDagResult.borderTargets().containsKey(name)) {
                            continue;
                        }

                        AccessRightSet arset = userDagResult.borderTargets().get(name);
                        ars.addAll(arset);

                        pcMap.put(endNodeName, ars);
                    }
                }
            }

            return new TargetDagResult(pcMap, reachedContainers);
        });
    }

    private Hashtable<String, Hashtable<String, Set<String>>> findBorderOaPrivRestrictedInternal(UserContext userCtx) throws PMException {
        // Uses a hashtable htReachableOas of reachable oas (see find_border_oa_priv(u))
        // An oa is a key in this hashtable. The value is another hashtable that
        // represents a label of the oa. A label is a set of pairs {(op -> pcset)}, with
        // the op being the key and pcset being the value.
        // {oa -> {op -> pcset}}.
        Hashtable<String, Hashtable<String, Set<String>>> htReachableOas = new Hashtable<>();

        // BFS from u (the base node). Prepare a queue.
        Set<String> visited = new HashSet<>();
        String crtNode;

        // Get u's directly assigned attributes and put them into the queue.
        List<String> hsAttrs = neo4jGraphStore.getParents(userCtx.getUser());
        List<String> queue = new ArrayList<>(hsAttrs);

        // While the queue has elements, extract an element from the queue
        // and visit it.
        while (!queue.isEmpty()) {
            // Extract an ua from queue.
            crtNode = queue.remove(0);
            if (!visited.contains(crtNode)) {
                // If the ua has ua -> oa edges
                if (inMemUattrHasOpsets(crtNode)) {
                    // Find the set of PCs reachable from ua.
                    HashSet<String> hsUaPcs = inMemFindPcSet(crtNode);

                    // From each discovered ua traverse the edges ua -> oa.

                    // Find the opsets of this user attribute. Note that the set of containers for this
                    // node (user attribute) may contain not only opsets.
                    List<Association> assocs = neo4jGraphStore.getAssociationsWithSource(crtNode);

                    // Go through the containers and only for opsets do the following.
                    // For each opset ops of ua:
                    for (Association assoc : assocs) {
                        String target = assoc.getTarget();
                        // If oa is in htReachableOas
                        if (htReachableOas.containsKey(target)) {
                            // Then oa has a label op1 -> hsPcs1, op2 -> hsPcs2,...
                            // Extract its label:
                            Hashtable<String, Set<String>> htOaLabel = htReachableOas.get(target);

                            // Get the operations from the opset:
                            AccessRightSet arSet = assoc.getAccessRightSet();
                            // For each operation in the opset
                            for (String sOp : arSet) {
                                // If the oa's label already contains the operation sOp
                                if (htOaLabel.containsKey(sOp)) {
                                    // The label contains op -> some pcset.
                                    // Do the union of the old pc with ua's pcset
                                    Set<String> hsPcs = htOaLabel.get(sOp);
                                    hsPcs.addAll(hsUaPcs);
                                } else {
                                    // The op is not in the oa's label.
                                    // Create new op -> ua's pcs mappiing in the label.
                                    Set<String> hsNewPcs = new HashSet<>(hsUaPcs);
                                    htOaLabel.put(sOp, hsNewPcs);
                                }
                            }
                        } else {
                            // oa is not in htReachableOas.
                            // Prepare a new label
                            Hashtable<String, Set<String>> htOaLabel = new Hashtable<>();

                            // Get the operations from the opset:
                            AccessRightSet arSet = assoc.getAccessRightSet();
                            // For each operation in the opset
                            for (String sOp : arSet) {
                                // Add op -> pcs to the label.
                                Set<String> hsNewPcs = new HashSet<>(hsUaPcs);
                                htOaLabel.put(sOp, hsNewPcs);
                            }

                            // Add oa -> {op -> pcs}
                            htReachableOas.put(target,  htOaLabel);
                        }
                    }
                }
                visited.add(crtNode);

                List<String> hsDescs = neo4jGraphStore.getParents(crtNode);
                queue.addAll(hsDescs);
            }
        }


        // For each reachable oa in htReachableOas.keys
        for (Enumeration<String> keys = htReachableOas.keys(); keys.hasMoreElements() ;) {
            String oa = keys.nextElement();
            // Compute {pc | oa ->+ pc}
            Set<String> hsOaPcs = inMemFindPcSet(oa);
            // Extract oa's label.
            Hashtable<String, Set<String>> htOaLabel = htReachableOas.get(oa);
            // The label contains op1 -> pcs1, op2 -> pcs2,...
            // For each operation in the label
            for (Enumeration<String> lbl = htOaLabel.keys(); lbl.hasMoreElements();) {
                String sOp = lbl.nextElement();
                // Intersect the pcset corresponding to this operation,
                // which comes from the uas, with the oa's pcset.
                Set<String> oaPcs = htOaLabel.get(sOp);
                oaPcs.retainAll(hsOaPcs);
                if (oaPcs.isEmpty()) htOaLabel.remove(sOp);
            }
        }

        return htReachableOas;
    }

    private HashSet<String> inMemFindPcSet(String node) throws PMException {
        HashSet<String> reachable = new HashSet<>();

        // Init the queue, visited
        ArrayList<String> queue = new ArrayList<>();
        HashSet<String> visited = new HashSet<>();

        // The current element
        String crtNode;

        // Insert the start node into the queue
        queue.add(node);

        List<String> policyClasses = neo4jGraphStore.getPolicyClasses();

        // While queue is not empty
        while (!queue.isEmpty()) {
            // Extract current element from queue
            crtNode = queue.remove(0);
            // If not visited
            if (!visited.contains(crtNode)) {
                // Mark it as visited
                visited.add(crtNode);
                // Extract its direct descendants. If a descendant is an attribute,
                // insert it into the queue. If it is a pc, add it to reachable,
                // if not already there
                List<String> hsContainers = neo4jGraphStore.getParents(crtNode);
                for (String n : hsContainers) {
                    if (policyClasses.contains(n)) {
                        reachable.add(n);
                    } else {
                        queue.add(n);
                    }
                }
            }
        }
        return reachable;
    }

    private boolean inMemUattrHasOpsets(String uaNode) throws PMException {
        return !neo4jGraphStore.getAssociationsWithSource(uaNode).isEmpty();
    }

    private List<EdgePath> explainDfs(String start) throws PMException {
        List<EdgePath> paths = new ArrayList<>();
        Map<String, List<EdgePath>> propPaths = new HashMap<>();

        Visitor visitor = nodeName -> {
            gov.nist.csd.pm.policy.model.graph.nodes.Node node = neo4jGraphStore.getNode(nodeName);
            List<EdgePath> nodePaths = new ArrayList<>();

            for(String parent : neo4jGraphStore.getParents(nodeName)) {
                gov.nist.csd.pm.policy.model.graph.relationships.Relationship edge = new gov.nist.csd.pm.policy.model.graph.relationships.Relationship(node.getName(), parent);
                List<EdgePath> parentPaths = propPaths.get(parent);
                if(parentPaths.isEmpty()) {
                    EdgePath path = new EdgePath();
                    path.addEdge(edge);
                    nodePaths.add(0, path);
                } else {
                    for(EdgePath p : parentPaths) {
                        EdgePath parentPath = new EdgePath();
                        for(gov.nist.csd.pm.policy.model.graph.relationships.Relationship e : p.getEdges()) {
                            parentPath.addEdge(new gov.nist.csd.pm.policy.model.graph.relationships.Relationship(e.getSource(), e.getTarget(), e.getAccessRightSet()));
                        }

                        parentPath.getEdges().add(0, edge);
                        nodePaths.add(parentPath);
                    }
                }
            }

            List<Association> assocs = neo4jGraphStore.getAssociationsWithSource(node.getName());
            for(Association association : assocs) {
                gov.nist.csd.pm.policy.model.graph.nodes.Node targetNode = neo4jGraphStore.getNode(association.getTarget());
                EdgePath path = new EdgePath();
                path.addEdge(new gov.nist.csd.pm.policy.model.graph.relationships.Relationship(node.getName(), targetNode.getName(), association.getAccessRightSet()));
                nodePaths.add(path);
            }

            // if the node being visited is the start node, add all the found nodePaths
            // we don't need the if for users, only when the target is an OA, so it might have something to do with
            // leafs vs non leafs
            if (node.getName().equals(start)) {
                paths.clear();
                paths.addAll(nodePaths);
            } else {
                propPaths.put(node.getName(), nodePaths);
            }
        };

        Propagator propagator = (parentNodeName, childNodeName) -> {
            gov.nist.csd.pm.policy.model.graph.nodes.Node parentNode = neo4jGraphStore.getNode(parentNodeName);
            gov.nist.csd.pm.policy.model.graph.nodes.Node childNode = neo4jGraphStore.getNode(childNodeName);
            List<EdgePath> childPaths = propPaths.computeIfAbsent(childNode.getName(), k -> new ArrayList<>());
            List<EdgePath> parentPaths = propPaths.get(parentNode.getName());

            for(EdgePath p : parentPaths) {
                EdgePath path = new EdgePath();
                for(gov.nist.csd.pm.policy.model.graph.relationships.Relationship edge : p.getEdges()) {
                    path.addEdge(new gov.nist.csd.pm.policy.model.graph.relationships.Relationship(edge.getSource(), edge.getTarget(), edge.getAccessRightSet()));
                }

                EdgePath newPath = new EdgePath();
                newPath.getEdges().addAll(path.getEdges());
                gov.nist.csd.pm.policy.model.graph.relationships.Relationship edge = new gov.nist.csd.pm.policy.model.graph.relationships.Relationship(childNode.getName(), parentNode.getName(), null);
                newPath.getEdges().add(0, edge);
                childPaths.add(newPath);
                propPaths.put(childNode.getName(), childPaths);
            }

            if (childNode.getName().equals(start)) {
                paths.clear();
                paths.addAll(propPaths.get(childNode.getName()));
            }
        };

        new DepthFirstGraphWalker(neo4jGraphStore)
                .withVisitor(visitor)
                .withPropagator(propagator)
                .withDirection(gov.nist.csd.pm.policy.model.graph.dag.walker.Direction.PARENTS)
                .walk(start);

        return paths;
    }
}
