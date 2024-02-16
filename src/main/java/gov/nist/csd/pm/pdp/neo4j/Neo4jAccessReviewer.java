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
        throw new PMException("not yet implemented");
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

            // process user prohibition paths
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

                    assocs.put(endNodeName, new AccessRightSet(arset));
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
                    AccessRightSet ars = pcMap.getOrDefault(endNodeName, new AccessRightSet());

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
