package gov.nist.csd.pm.impl.neo4j.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.modification.GraphModification;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.graph.relationship.InvalidAssignmentException;
import gov.nist.csd.pm.common.graph.relationship.InvalidAssociationException;
import gov.nist.csd.pm.pap.exception.*;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.graphdb.traversal.Uniqueness;

import java.util.*;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;
import static gov.nist.csd.pm.pap.AdminPolicyNode.POLICY_CLASS_TARGETS;

public class Neo4JGraphModification implements GraphModification {

    public static final Label NODE_LABEL = Label.label("Node");
    public static final Label PC_LABEL = Label.label("PC");
    public static final Label OA_LABEL = Label.label("OA");
    public static final Label UA_LABEL = Label.label("UA");
    public static final Label O_LABEL = Label.label("O");
    public static final Label U_LABEL = Label.label("U");
    public static final Label RES_ARS_LABEL = Label.label("RES_ARS");
    public static final String ARSET_PROPERTY = "arset";

    public static final String NAME_PROPERTY = "name";

    public static final RelationshipType ASSIGNMENT_RELATIONSHIP_TYPE = RelationshipType.withName("ASSIGNED_TO");
    public static final RelationshipType ASSOCIATION_RELATIONSHIP_TYPE = RelationshipType.withName("ASSOCIATED_WITH");

    private Neo4jConnection neo4j;

    public Neo4JGraphModification(Neo4jConnection neo4j) {
        this.neo4j = neo4j;
    }

    @Override
    public void setResourceAccessRights(AccessRightSet accessRightSet)
            throws AdminAccessRightExistsException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                checkSetResourceAccessRightsInput(accessRightSet);

                try(ResourceIterator<org.neo4j.graphdb.Node> arNodesIter = tx.findNodes(RES_ARS_LABEL)) {
                    // delete the existing RES_ARS node
                    if (arNodesIter.hasNext()) {
                        org.neo4j.graphdb.Node node = arNodesIter.next();
                        node.delete();
                    }

                    // create a new one with the updated access rights
                    org.neo4j.graphdb.Node node = tx.createNode(RES_ARS_LABEL);
                    node.setProperty(ARSET_PROPERTY, accessRightSet.toArray(String[]::new));
                }
            });
        } catch (AdminAccessRightExistsException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public AccessRightSet getResourceAccessRights() throws PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                try (ResourceIterator<org.neo4j.graphdb.Node> arNodesIter = tx.findNodes(RES_ARS_LABEL)) {
                    if (!arNodesIter.hasNext()) {
                        return new AccessRightSet();
                    }

                    org.neo4j.graphdb.Node node = arNodesIter.next();
                    String[] arset = (String[]) node.getProperty(ARSET_PROPERTY);

                    return new AccessRightSet(arset);
                }
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public String createPolicyClass(String name, Map<String, String> properties)
            throws NodeNameExistsException, PMBackendException {
        try {
            createPolicyClassNode(name, properties);
        } catch (NodeNameExistsException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }

        return name;
    }

    @Override
    public String createUserAttribute(String name, Map<String, String> properties, List<String> parents)
            throws NodeNameExistsException, NodeDoesNotExistException, InvalidAssignmentException, PMBackendException,
            AssignmentCausesLoopException, DisconnectedNodeException {
        try {
            neo4j.runTx(tx -> {
                org.neo4j.graphdb.Node node = createNode(name, properties, UA_LABEL, parents);
                assignNode(node, parents);
            });
        } catch (NodeNameExistsException | NodeDoesNotExistException | InvalidAssignmentException | PMBackendException |
                 AssignmentCausesLoopException | DisconnectedNodeException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }

        return name;
    }

    @Override
    public String createObjectAttribute(String name, Map<String, String> properties, List<String> parents)
            throws NodeNameExistsException, NodeDoesNotExistException, InvalidAssignmentException, PMBackendException,
                   AssignmentCausesLoopException, DisconnectedNodeException {
        try {
            neo4j.runTx(tx -> {
                org.neo4j.graphdb.Node node = createNode(name, properties, OA_LABEL, parents);
                assignNode(node, parents);
            });
        } catch (NodeNameExistsException | NodeDoesNotExistException | InvalidAssignmentException | PMBackendException |
                 AssignmentCausesLoopException | DisconnectedNodeException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }

        return name;
    }

    @Override
    public String createObject(String name, Map<String, String> properties, List<String> parents)
            throws NodeNameExistsException, NodeDoesNotExistException, InvalidAssignmentException, PMBackendException,
                   AssignmentCausesLoopException, DisconnectedNodeException {
        try {
            neo4j.runTx(tx -> {
                org.neo4j.graphdb.Node node = createNode(name, properties, O_LABEL, parents);
                assignNode(node, parents);
            });
        } catch (NodeNameExistsException | NodeDoesNotExistException | InvalidAssignmentException | PMBackendException |
                 AssignmentCausesLoopException | DisconnectedNodeException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }

        return name;
    }

    @Override
    public String createUser(String name, Map<String, String> properties, List<String> parents)
            throws NodeNameExistsException, NodeDoesNotExistException, InvalidAssignmentException, PMBackendException,
                   AssignmentCausesLoopException, DisconnectedNodeException {
        try {
            neo4j.runTx(tx -> {
                org.neo4j.graphdb.Node node = createNode(name, properties, U_LABEL, parents);
                assignNode(node, parents);
            });
        } catch (NodeNameExistsException | NodeDoesNotExistException | InvalidAssignmentException | PMBackendException |
                 AssignmentCausesLoopException | DisconnectedNodeException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }

        return name;
    }

    @Override
    public void setNodeProperties(String name, Map<String, String> properties)
            throws NodeDoesNotExistException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                checkSetNodePropertiesInput(name);

                org.neo4j.graphdb.Node node = tx.findNode(NODE_LABEL, NAME_PROPERTY, name);
                for (String key : node.getPropertyKeys()) {
                    // do not remove name property
                    if (key.equals(NAME_PROPERTY)) {
                        continue;
                    }

                    node.removeProperty(key);
                }

                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    node.setProperty(entry.getKey(), entry.getValue());
                }
            });
        } catch (NodeDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public boolean nodeExists(String name) throws PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                return tx.findNode(NODE_LABEL, NAME_PROPERTY, name) != null;
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public Node getNode(String name) throws NodeDoesNotExistException, PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                checkGetNodeInput(name);

                org.neo4j.graphdb.Node neoNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, name);

                // get node type
                NodeType type = PC;
                for (Label label : neoNode.getLabels()) {
                    type = labelToType(label);
                    if (type != null) {
                        break;
                    }
                }

                Map<String, Object> neoProps = neoNode.getAllProperties();
                Map<String, String> nodeProps = new HashMap<>();
                neoProps.forEach((k, v) -> {
                    if (k.equals(NAME_PROPERTY)) {
                        return;
                    }

                    nodeProps.put(k, String.valueOf(v));
                });

                return new Node(name, type, nodeProps);
            });
        } catch (NodeDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public List<String> search(NodeType type, Map<String, String> properties) throws PMBackendException {
        try {
            Label label;
            if (type == null || type == ANY) {
                label = NODE_LABEL;
            } else {
                label = Label.label(type.toString());
            }

            List<org.neo4j.graphdb.Node> results = new ArrayList<>();

            return neo4j.runTx(tx -> {
                try(ResourceIterator<org.neo4j.graphdb.Node> iter = tx.findNodes(label)) {
                    while (iter.hasNext()) {
                        org.neo4j.graphdb.Node next = iter.next();
                        Map<String, Object> allProperties = next.getAllProperties();

                        if (!hasAllKeys(allProperties, properties)
                                || !valuesMatch(allProperties, properties)) {
                            continue;
                        }

                        results.add(next);
                    }
                }

                return results.stream()
                              .map(n -> String.valueOf(n.getProperty(NAME_PROPERTY)))
                              .toList();
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public List<String> getPolicyClasses() throws PMBackendException {
        try {
            List<String> pcs = new ArrayList<>();

            neo4j.runTx(tx -> {
                try(ResourceIterator<org.neo4j.graphdb.Node> iter = tx.findNodes(PC_LABEL)) {
                    while (iter.hasNext()) {
                        pcs.add(String.valueOf(iter.next().getProperty(NAME_PROPERTY)));
                    }
                }
            });

            return pcs;
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }

    }

    @Override
    public void deleteNode(String name)
            throws NodeHasChildrenException, NodeReferencedInProhibitionException, NodeReferencedInObligationException,
                   PMBackendException {
        try {
            neo4j.runTx(tx -> {
                if (!checkDeleteNodeInput(name, new Neo4JProhibitionsModification(neo4j), new Neo4JObligationsModification(neo4j))) {
                    return;
                }

                org.neo4j.graphdb.Node node = tx.findNode(NODE_LABEL, NAME_PROPERTY, name);

                // delete rels
                ResourceIterator<Relationship> rels = node.getRelationships().iterator();
                while (rels.hasNext()) {
                    rels.next().delete();
                }

                // if the node is a pc, delete the target node
                if (node.hasLabel(PC_LABEL)) {
                    org.neo4j.graphdb.Node targetNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, AdminPolicy.policyClassTargetName(name));

                    // delete rels
                    rels = targetNode.getRelationships().iterator();
                    while (rels.hasNext()) {
                        rels.next().delete();
                    }

                    // delete target ndoe
                    targetNode.delete();
                }

                // delete node
                node.delete();
            });
        } catch (NodeHasChildrenException | NodeReferencedInProhibitionException | NodeReferencedInObligationException |
                 PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void assign(String child, String parent)
            throws NodeDoesNotExistException, InvalidAssignmentException, PMBackendException,
                   AssignmentCausesLoopException {
        try {
            neo4j.runTx(tx -> {
                if (!checkAssignInput(child, parent)) {
                    return;
                }

                tx.findNode(NODE_LABEL, NAME_PROPERTY, child)
                  .createRelationshipTo(tx.findNode(NODE_LABEL, NAME_PROPERTY, parent), ASSIGNMENT_RELATIONSHIP_TYPE);
            });
        } catch (NodeDoesNotExistException | InvalidAssignmentException | PMBackendException |
                 AssignmentCausesLoopException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void deassign(String child, String parent)
            throws NodeDoesNotExistException, DisconnectedNodeException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                if (!checkDeassignInput(child, parent)) {
                    return;
                }

                org.neo4j.graphdb.Node childNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, child);
                try(ResourceIterator<Relationship> relationships = childNode.getRelationships(Direction.OUTGOING, ASSIGNMENT_RELATIONSHIP_TYPE).iterator()) {
                    while (relationships.hasNext()) {
                        Relationship parentRelationship = relationships.next();
                        if (parentRelationship.getEndNode().getProperty(NAME_PROPERTY).equals(parent)) {
                            parentRelationship.delete();
                        }
                    }
                }
            });
        } catch (NodeDoesNotExistException | DisconnectedNodeException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public List<String> getParents(String name) throws NodeDoesNotExistException, PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                checkGetParentsInput(name);

                org.neo4j.graphdb.Node node = tx.findNode(NODE_LABEL, NAME_PROPERTY, name);
                try(ResourceIterator<Relationship> iter = node.getRelationships(
                        Direction.OUTGOING,
                        ASSIGNMENT_RELATIONSHIP_TYPE
                ).iterator()) {
                    List<String> parents = new ArrayList<>();
                    while (iter.hasNext()) {
                        Relationship next = iter.next();
                        parents.add(String.valueOf(next.getEndNode().getProperty(NAME_PROPERTY)));
                    }

                    return parents;
                }
            });
        } catch (NodeDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public List<String> getChildren(String name) throws NodeDoesNotExistException, PMBackendException {
        try {
            return neo4j.runTx(tx-> {
                checkGetChildrenInput(name);

                org.neo4j.graphdb.Node node = tx.findNode(NODE_LABEL, NAME_PROPERTY, name);
                try(ResourceIterator<Relationship> iter = node.getRelationships(
                        Direction.INCOMING,
                        ASSIGNMENT_RELATIONSHIP_TYPE
                ).iterator()) {
                    List<String> children = new ArrayList<>();

                    while (iter.hasNext()) {
                        Relationship next = iter.next();
                        children.add(next.getStartNode().getProperty(NAME_PROPERTY).toString());
                    }

                    return children;
                }
            });
        } catch (NodeDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void associate(String ua, String target, AccessRightSet accessRights)
            throws NodeDoesNotExistException, InvalidAssociationException, PMBackendException,
            UnknownAccessRightException {
        try {
            neo4j.runTx(tx -> {
                checkAssociateInput(ua, target, accessRights);

                org.neo4j.graphdb.Node uaNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, ua);
                org.neo4j.graphdb.Node targetNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, target);

                ResourceIterator<Relationship> assocs = uaNode.getRelationships(
                        Direction.OUTGOING,
                        ASSOCIATION_RELATIONSHIP_TYPE
                ).iterator();

                // delete the assoc if it already exists
                while (assocs.hasNext()) {
                    Relationship next = assocs.next();
                    org.neo4j.graphdb.Node t = next.getEndNode();
                    if (t.equals(targetNode)) {
                        next.delete();
                        break;
                    }
                }

                Relationship association = uaNode.createRelationshipTo(targetNode, ASSOCIATION_RELATIONSHIP_TYPE);
                association.setProperty(ARSET_PROPERTY, accessRights.toArray(new String[]{}));
            });
        } catch (NodeDoesNotExistException | InvalidAssociationException | PMBackendException |
                 UnknownAccessRightException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void dissociate(String ua, String target) throws NodeDoesNotExistException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                if (!checkDissociateInput(ua, target)) {
                    return;
                }

                org.neo4j.graphdb.Node uaNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, ua);
                try (ResourceIterator<Relationship> relationships = uaNode.getRelationships(
                        Direction.OUTGOING,
                        ASSOCIATION_RELATIONSHIP_TYPE
                ).iterator()) {
                    while (relationships.hasNext()) {
                        Relationship assocRel = relationships.next();
                        if (assocRel.getEndNode().getProperty(NAME_PROPERTY).equals(target)) {
                            assocRel.delete();
                        }
                    }
                }
            });
        } catch (NodeDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public List<Association> getAssociationsWithSource(String ua) throws NodeDoesNotExistException, PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                checkGetAssociationsWithSourceInput(ua);

                org.neo4j.graphdb.Node node = tx.findNode(NODE_LABEL, NAME_PROPERTY, ua);
                try (ResourceIterator<Relationship> iter = node.getRelationships(
                        Direction.OUTGOING,
                        ASSOCIATION_RELATIONSHIP_TYPE
                ).iterator()) {
                    List<Association> assocs = new ArrayList<>();
                    while (iter.hasNext()) {
                        Relationship next = iter.next();
                        String[] arArr = (String[])next.getProperty(ARSET_PROPERTY);
                        AccessRightSet arset = new AccessRightSet(arArr);

                        assocs.add(new Association(
                                ua,
                                String.valueOf(next.getEndNode().getProperty(NAME_PROPERTY)),
                                arset
                        ));
                    }

                    return assocs;
                }
            });
        } catch (NodeDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public List<Association> getAssociationsWithTarget(String target)
            throws NodeDoesNotExistException, PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                checkGetAssociationsWithTargetInput(target);

                org.neo4j.graphdb.Node node = tx.findNode(NODE_LABEL, NAME_PROPERTY, target);
                try (ResourceIterator<Relationship> iter = node.getRelationships(
                        Direction.INCOMING,
                        ASSOCIATION_RELATIONSHIP_TYPE
                ).iterator()) {
                    List<Association> assocs = new ArrayList<>();
                    while (iter.hasNext()) {
                        Relationship next = iter.next();
                        String[] arArr = (String[])next.getProperty(ARSET_PROPERTY);
                        AccessRightSet arset = new AccessRightSet(arArr);

                        assocs.add(new Association(
                                String.valueOf(next.getStartNode().getProperty(NAME_PROPERTY)),
                                target,
                                arset
                        ));
                    }

                    return assocs;
                }
            });
        } catch (NodeDoesNotExistException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void checkAssignmentDoesNotCreateLoop(String child, String parent)
            throws AssignmentCausesLoopException, PMBackendException {
        // handle self loop case
        if (child.equals(parent)) {
            throw new AssignmentCausesLoopException(child, parent);
        }

        try {
            neo4j.runTx(tx -> {
                org.neo4j.graphdb.Node parentNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, parent);
                Traverser traverse = tx.traversalDescription()
                                       .breadthFirst()
                                       .relationships(ASSIGNMENT_RELATIONSHIP_TYPE, Direction.OUTGOING)
                                       .uniqueness(Uniqueness.NONE)
                                       .evaluator(path -> {
                                           Relationship last = path.lastRelationship();
                                           if (last == null) {
                                               return Evaluation.EXCLUDE_AND_CONTINUE;
                                           } else if (last.getEndNode().getProperty(NAME_PROPERTY).equals(child)) {
                                               return Evaluation.INCLUDE_AND_PRUNE;
                                           }

                                           return Evaluation.EXCLUDE_AND_CONTINUE;
                                       })
                                       .traverse(parentNode);

                if (traverse.iterator().hasNext()) {
                    throw new AssignmentCausesLoopException(child, parent);
                }
            });
        } catch (AssignmentCausesLoopException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    public void createNodeInternal(String name, NodeType type, Map<String, String> properties)
            throws PMBackendException {
        try {
            Label typeLabel = typeToLabel(type);

            neo4j.runTx(tx -> {
                org.neo4j.graphdb.Node node = tx.createNode(NODE_LABEL, typeLabel);
                node.setProperty(NAME_PROPERTY, name);
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    node.setProperty(entry.getKey(), entry.getValue());
                }
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    public void assignInternal(String child, String parent) throws PMBackendException {
        try {
            neo4j.runTx(tx -> {
                org.neo4j.graphdb.Node childNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, child);
                org.neo4j.graphdb.Node parentNode = tx.findNode(NODE_LABEL, NAME_PROPERTY, parent);
                childNode.createRelationshipTo(parentNode, ASSIGNMENT_RELATIONSHIP_TYPE);
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    private String createPolicyClassNode(String name, Map<String, String> properties)
            throws PMException {
        neo4j.runTx(tx -> {
            checkCreatePolicyClassInput(name);

            createNodeInternal(name, PC, properties);

            // create pc rep oa or verify that its assigned to the POLICY_CLASS_TARGETS node if already created
            String pcTarget = AdminPolicy.policyClassTargetName(name);
            if (!nodeExists(pcTarget)) {
                createNodeInternal(pcTarget, OA, new HashMap<>());
            }

            try {
                if (!getParents(pcTarget).contains(POLICY_CLASS_TARGETS.nodeName())) {
                    assignInternal(pcTarget, POLICY_CLASS_TARGETS.nodeName());
                }
            } catch (NodeDoesNotExistException e) {
                throw new PMBackendException("error creating target attribute for policy class " + name, e);
            }
        });

        return name;
    }

    private org.neo4j.graphdb.Node createNode(String name, Map<String, String> properties, Label typeLabel, List<String> parents)
            throws PMException {
        Map<String, String> props = new HashMap<>();
        if (properties != null) {
            props.putAll(properties);
        }

        return neo4j.runTx(tx -> {
            if (typeLabel.equals(PC_LABEL)) {
                checkCreatePolicyClassInput(name);
            } else {
                checkCreateNodeInput(name, labelToType(typeLabel), parents);
            }

            org.neo4j.graphdb.Node node = tx.createNode(NODE_LABEL, typeLabel);
            node.setProperty(NAME_PROPERTY, name);
            for (Map.Entry<String, String> entry : props.entrySet()) {
                node.setProperty(entry.getKey(), entry.getValue());
            }

            return node;
        });
    }

    private void assignNode(org.neo4j.graphdb.Node node, List<String> parents) throws PMException {
        neo4j.runTx(tx -> {
            for (String parent : parents) {
                node.createRelationshipTo(tx.findNode(NODE_LABEL, NAME_PROPERTY, parent), ASSIGNMENT_RELATIONSHIP_TYPE);
            }
        });
    }

    private boolean valuesMatch(Map<String, Object> nodeProperties, Map<String, String> searchProperties) {
        for (Map.Entry<String, String> entry : searchProperties.entrySet()) {
            String checkKey = entry.getKey();
            String checkValue = entry.getValue();
            if (!checkValue.equals(nodeProperties.get(checkKey))
                    && !checkValue.equals("*")) {
                return false;
            }
        }

        return true;
    }

    private boolean hasAllKeys(Map<String, Object> nodeProperties, Map<String, String> searchProperties) {
        for (String key : searchProperties.keySet()) {
            // ignore the name property
            if (key.equals(NAME_PROPERTY)) {
                continue;
            }

            if (!nodeProperties.containsKey(key)) {
                return false;
            }
        }

        return true;
    }

    private Label typeToLabel(NodeType type) {
        Label typeLabel;
        if (type == PC) {
            typeLabel = PC_LABEL;
        } else if (type == OA) {
            typeLabel = OA_LABEL;
        } else if (type == UA) {
            typeLabel = UA_LABEL;
        } else if (type == O) {
            typeLabel = O_LABEL;
        } else {
            typeLabel = U_LABEL;
        }

        return typeLabel;
    }

    private NodeType labelToType(Label label) {
        if (label.equals(OA_LABEL)) {
            return OA;
        } else if (label.equals(UA_LABEL)) {
            return NodeType.UA;
        } else if (label.equals(O_LABEL)) {
            return NodeType.O;
        } else if (label.equals(U_LABEL)) {
            return NodeType.U;
        } else if (label.equals(PC_LABEL)) {
            return PC;
        }

        return null;
    }
}
