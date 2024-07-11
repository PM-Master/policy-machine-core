package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.dag.DepthFirstGraphWalker;
import gov.nist.csd.pm.common.graph.dag.Direction;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Assignment;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.pattern.Pattern;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;
import static gov.nist.csd.pm.pap.admin.AdminPolicyNode.POLICY_CLASS_TARGETS;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.wildcardAccessRights;

public abstract class GraphModifier extends Modifier implements GraphModification {

    protected abstract void setResourceAccessRightsInternal(AccessRightSet accessRightSet) throws PMException;
    protected abstract void createNodeInternal(String name, NodeType type, Map<String, String> properties) throws PMException;
    protected abstract void deleteNodeInternal(String name) throws PMException;
    protected abstract void setNodePropertiesInternal(String name, Map<String, String> properties) throws PMException;
    protected abstract void createAssignmentInternal(String start, String end) throws PMException;
    protected abstract void deleteAssignmentInternal(String start, String end) throws PMException;
    protected abstract void createAssociationInternal(String ua, String target, AccessRightSet arset) throws PMException;
    protected abstract void deleteAssociationInternal(String ua, String target) throws PMException;

    @Override
    public void setResourceAccessRights(AccessRightSet accessRightSet) throws PMException {
        checkSetResourceAccessRightsInput(accessRightSet);

        setResourceAccessRightsInternal(accessRightSet);
    }

    @Override
    public String createPolicyClass(String name, Map<String, String> properties) throws PMException {
        return runTx(() -> {
            checkCreatePolicyClassInput(name);

            // create pc node
            createNodeInternal(name, PC, properties);

            // create pc target oa or verify that its assigned to the POLICY_CLASS_TARGETS node if already created
            String pcTarget = AdminPolicy.policyClassTargetName(name);
            if (!query().graph().nodeExists(pcTarget)) {
                createNodeInternal(pcTarget, OA, new HashMap<>());
            }

            Collection<String> descendants = query().graph().getAdjacentDescendants(pcTarget);
            if (!descendants.contains(POLICY_CLASS_TARGETS.nodeName())) {
                createAssignmentInternal(pcTarget, POLICY_CLASS_TARGETS.nodeName());
            }

            return name;
        });
    }

    @Override
    public String createUserAttribute(String name, Map<String, String> properties, Collection<String> assignments)
            throws PMException {
        return createNonPolicyClassNode(name, UA, properties, assignments);
    }

    @Override
    public String createObjectAttribute(String name, Map<String, String> properties, Collection<String> assignments)
            throws PMException {
        return createNonPolicyClassNode(name, OA, properties, assignments);
    }

    @Override
    public String createObject(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        return createNonPolicyClassNode(name, O, properties, assignments);
    }

    @Override
    public String createUser(String name, Map<String, String> properties, Collection<String> assignments) throws PMException {
        return createNonPolicyClassNode(name, U, properties, assignments);
    }

    @Override
    public void setNodeProperties(String name, Map<String, String> properties) throws PMException {
        checkSetNodePropertiesInput(name);

        setNodePropertiesInternal(name, properties);
    }

    @Override
    public void deleteNode(String name) throws PMException {
        runTx(() -> {
            if(!checkDeleteNodeInput(name)) {
                return;
            }

            NodeType type = query().graph().getNode(name).getType();
            if (type == PC) {
                String rep = AdminPolicy.policyClassTargetName(name);
                deleteNodeInternal(rep);
            }

            deleteNodeInternal(name);
        });
    }

    @Override
    public void assign(String ascendant, String descendant) throws PMException {
        if(!checkAssignInput(ascendant, descendant)) {
            return;
        }

        createAssignmentInternal(ascendant, descendant);
    }

    @Override
    public void deassign(String ascendant, String descendant) throws PMException {
        if(!checkDeassignInput(ascendant, descendant)) {
            return;
        }

        deleteAssignmentInternal(ascendant, descendant);
    }

    @Override
    public void associate(String ua, String target, AccessRightSet accessRights) throws PMException {
        checkAssociateInput(ua, target, accessRights);

        createAssociationInternal(ua, target, accessRights);
    }

    @Override
    public void dissociate(String ua, String target) throws PMException {
        if(!checkDissociateInput(ua, target)) {
            return;
        }

        deleteAssociationInternal(ua, target);
    }

    /**
     * Check if a proposed assignment causes a loop.
     *
     * @param ascendant  The ascendant of the assignment.
     * @param descendant The descendant of the assignment.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkAssignmentDoesNotCreateLoop(String ascendant, String descendant) throws PMException {
        AtomicBoolean loop = new AtomicBoolean(false);

        new DepthFirstGraphWalker(this.query().graph())
                .withVisitor((node -> {
                    if (!node.equals(ascendant)) {
                        return;
                    }

                    loop.set(true);
                }))
                .withDirection(Direction.DESCENDANTS)
                .withAllPathShortCircuit(node -> node.equals(ascendant))
                .walk(descendant);

        if (loop.get()) {
            throw new AssignmentCausesLoopException(ascendant, descendant);
        }
    }

    /**
     * Check that the provided resource access rights are not existing admin access rights.
     *
     * @param accessRightSet The access right set to check.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkSetResourceAccessRightsInput(AccessRightSet accessRightSet) throws PMException {
        for (String ar : accessRightSet) {
            if (isAdminAccessRight(ar) || isWildcardAccessRight(ar)) {
                throw new AdminAccessRightExistsException(ar);
            }
        }
    }

    /**
     * Check that the given policy class name is not taken by another node.
     *
     * @param name The name to check.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkCreatePolicyClassInput(String name) throws PMException {
        if (query().graph().nodeExists(name)) {
            throw new NodeNameExistsException(name);
        }
    }

    /**
     * Check the node name does not already exist and ensure the given descendant nodes exist and form a valid assignment.
     *
     * @param name    The name of the new node.
     * @param type    The type of the new node.
     * @param assignments Nodes to assign the new node to.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     * assignment.
     */
    protected void checkCreateNodeInput(String name, NodeType type, Collection<String> assignments) throws PMException {
        if (query().graph().nodeExists(name)) {
            throw new NodeNameExistsException(name);
        }

        // when creating a node the only loop that can occur is to itself
        if (assignments.contains(name)) {
            throw new AssignmentCausesLoopException(name, name);
        }

        // need to be assigned to at least one node to avoid a disconnected graph
        if (assignments.isEmpty()) {
            throw new DisconnectedNodeException(name, type);
        }

        // check assign inputs
        for (String p : assignments) {
            if (name.equals(p)) {
                throw new AssignmentCausesLoopException(name, p);
            }

            Node assignNode = query().graph().getNode(p);
            Assignment.checkAssignment(type, assignNode.getType());
        }
    }

    /**
     * Check if the given nodes exists.
     *
     * @param name The name of the node to check.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkSetNodePropertiesInput(String name) throws PMException {
        if (!query().graph().nodeExists(name)) {
            throw new NodeDoesNotExistException(name);
        }
    }

    /**
     * Check if the given node can be deleted. If the node is referenced in a prohibition or event pattern then it
     * cannot
     * be deleted. If the node does not exist an error does not occur but return false to indicate to the caller that
     * execution should not proceed.
     *
     * @param name              The name of the node being deleted.
     *                          pattern.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected boolean checkDeleteNodeInput(String name) throws PMException {
        Collection<String> ascendants;
        try {
            ascendants = query().graph().getAdjacentAscendants(name);
        } catch (NodeDoesNotExistException e) {
            // quietly return if the nodes already does not exist as this is the desired state
            return false;
        }

        if (!ascendants.isEmpty()) {
            throw new NodeHasAscendantsException(name);
        }

        checkIfNodeInProhibition(name);
        checkIfNodeInObligation(name);

        return true;
    }

    /**
     * Helper method to check if a given node is referenced in any prohibitions. The default implementation loads all
     * prohibitions into memory and then searches through each one.
     *
     * @param name             The node to check for.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkIfNodeInProhibition(String name) throws PMException {
        Map<String, Collection<Prohibition>> allProhibitions = query().prohibitions().getAll();
        for (Collection<Prohibition> subjPros : allProhibitions.values()) {
            for (Prohibition p : subjPros) {
                if (nodeInProhibition(name, p)) {
                    throw new NodeReferencedInProhibitionException(name, p.getName());
                }
            }
        }
    }

    /**
     * Helper method to check if a given node is referenced in any obligations. The default implementation loads all
     * obligations into memory and then searches through each one.
     *
     * @param name             The node to check for.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkIfNodeInObligation(String name) throws PMException {
        Collection<Obligation> obligations = query().obligations().getAll();
        for (Obligation obligation : obligations) {
            // if the node is the author of the obligation or referenced in any rules throw an exception
            if (obligation.getAuthor().getUser().equals(name)) {
                throw new NodeReferencedInObligationException(name, obligation.getName());
            }

            // check if node referenced in pattern
            for (Rule rule : obligation.getRules()) {
                EventPattern eventPattern = rule.getEventPattern();

                // check subject and operation patterns
                boolean referenced = checkPatternForNode(name, eventPattern.getSubjectPattern());

                // check operand patterns
                for (Pattern pattern : eventPattern.getOperandPatterns()) {
                    if (checkPatternForNode(name, pattern)) {
                        referenced = true;
                    }
                }

                if (referenced) {
                    throw new NodeReferencedInObligationException(name, obligation.getName());
                }
            }
        }
    }

    private boolean checkPatternForNode(String entity, Pattern pattern) {
        return pattern.getReferencedNodes().nodes().contains(entity);
    }

    /**
     * Check if both nodes exist and make a valid assignment. If the assignment already exists an error does not
     * occur but
     * return false to indicate to the caller that execution should not proceed.
     *
     * @param ascendant  The ascendant node.
     * @param descendant The descendant node.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected boolean checkAssignInput(String ascendant, String descendant) throws PMException {
        // ignore if assignment already exists
        if (query().graph().getAdjacentDescendants(ascendant).contains(descendant)) {
            return false;
        }

        // getting both nodes will check if they exist
        Node ascNode = query().graph().getNode(ascendant);
        Node descNode = query().graph().getNode(descendant);

        // check node types make a valid assignment relation
        Assignment.checkAssignment(ascNode.getType(), descNode.getType());

        // check the assignment won't create a loop
        checkAssignmentDoesNotCreateLoop(ascendant, descendant);

        return true;
    }

    /**
     * Check if both nodes exist. If the assignment does not exist an error does not occur but return false to indicate
     * to the caller that execution should not proceed.
     *
     * @param ascendant  The ascendant node.
     * @param descendant The descendant node.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected boolean checkDeassignInput(String ascendant, String descendant) throws PMException {
        if (!query().graph().nodeExists(ascendant)) {
            throw new NodeDoesNotExistException(ascendant);
        } else if (!query().graph().nodeExists(descendant)) {
            throw new NodeDoesNotExistException(descendant);
        }

        Collection<String> descs = query().graph().getAdjacentDescendants(ascendant);
        if (!descs.contains(descendant)) {
            return false;
        }

        if (descs.size() == 1) {
            throw new DisconnectedNodeException(ascendant, descendant);
        }

        return true;
    }

    /**
     * Check if the user attribute and target nodes exist and make up a valid association and that the given access
     * rights are allowed.
     *
     * @param ua           The user attribute.
     * @param target       The target node.
     * @param accessRights The access rights.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     * association.
     */
    protected void checkAssociateInput(String ua, String target, AccessRightSet accessRights) throws PMException {
        Node uaNode = query().graph().getNode(ua);
        Node targetNode = query().graph().getNode(target);

        // check the access rights are valid
        checkAccessRightsValid(query().graph().getResourceAccessRights(), accessRights);

        // check the types of each node make a valid association
        Association.checkAssociation(uaNode.getType(), targetNode.getType());
    }

    /**
     * Check if both nodes exist. If the association does not exist an error does not occur but return false to indicate
     * to the caller that execution should not proceed.
     *
     * @param ua     The user attribute.
     * @param target The target node.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected boolean checkDissociateInput(String ua, String target) throws PMException {
        if (!query().graph().nodeExists(ua)) {
            throw new NodeDoesNotExistException(ua);
        } else if (!query().graph().nodeExists(target)) {
            throw new NodeDoesNotExistException(target);
        }

        Collection<Association> associations = query().graph().getAssociationsWithSource(ua);
        for (Association a : associations) {
            if (a.getSource().equals(ua) && a.getTarget().equals(target)) {
                return true;
            }
        }

        return false;
    }

    static void checkAccessRightsValid(AccessRightSet resourceAccessRights, AccessRightSet accessRightSet) throws PMException {
        for (String ar : accessRightSet) {
            if (!resourceAccessRights.contains(ar)
                    && !allAdminAccessRights().contains(ar)
                    && !wildcardAccessRights().contains(ar)) {
                throw new UnknownAccessRightException(ar);
            }
        }
    }

    private static boolean nodeInProhibition(String name, Prohibition prohibition) {
        if (prohibition.getSubject().getName().equals(name)) {
            return true;
        }

        for (ContainerCondition containerCondition : prohibition.getContainers()) {
            if (containerCondition.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    private String createNonPolicyClassNode(String name, NodeType type, Map<String, String> properties, Collection<String> assignments)
            throws PMException {
        return runTx(() -> {
            checkCreateNodeInput(name, type, assignments);

            createNodeInternal(name, type, properties);

            for (String assignmentNode : assignments) {
                createAssignmentInternal(name, assignmentNode);
            }

            return name;
        });

    }
}
