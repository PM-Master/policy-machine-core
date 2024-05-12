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
import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.pattern.Pattern;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;
import static gov.nist.csd.pm.pap.AdminPolicyNode.POLICY_CLASS_TARGETS;
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

            Collection<String> parents = query().graph().getParents(pcTarget);
            if (!parents.contains(POLICY_CLASS_TARGETS.nodeName())) {
                createAssignmentInternal(pcTarget, POLICY_CLASS_TARGETS.nodeName());
            }

            return name;
        });
    }

    @Override
    public String createUserAttribute(String name, Map<String, String> properties, Collection<String> parents)
            throws PMException {
        return createNonPolicyClassNode(name, UA, properties, parents);
    }

    @Override
    public String createObjectAttribute(String name, Map<String, String> properties, Collection<String> parents)
            throws PMException {
        return createNonPolicyClassNode(name, OA, properties, parents);
    }

    @Override
    public String createObject(String name, Map<String, String> properties, Collection<String> parents) throws PMException {
        return createNonPolicyClassNode(name, O, properties, parents);
    }

    @Override
    public String createUser(String name, Map<String, String> properties, Collection<String> parents) throws PMException {
        return createNonPolicyClassNode(name, U, properties, parents);
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
    public void assign(String child, String parent) throws PMException {
        if(!checkAssignInput(child, parent)) {
            return;
        }

        createAssignmentInternal(child, parent);
    }

    @Override
    public void deassign(String child, String parent) throws PMException {
        if(!checkDeassignInput(child, parent)) {
            return;
        }

        deleteAssignmentInternal(child, parent);
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
     * @param child  The child of the assignment.
     * @param parent The parent of the assignment.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkAssignmentDoesNotCreateLoop(String child, String parent) throws PMException {
        AtomicBoolean loop = new AtomicBoolean(false);

        new DepthFirstGraphWalker(this.query().graph())
                .withVisitor((node -> {
                    if (!node.equals(child)) {
                        return;
                    }

                    loop.set(true);
                }))
                .withDirection(Direction.PARENTS)
                .withAllPathShortCircuit(node -> node.equals(child))
                .walk(parent);

        if (loop.get()) {
            throw new AssignmentCausesLoopException(child, parent);
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
     * Check the node name does not already exist and ensure the give parent nodes exist and make up a valid assignment.
     *
     * @param name    The name of the new node.
     * @param type    The type of the new node.
     * @param parents Parents to assign the new node to.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     * assignment.
     */
    protected void checkCreateNodeInput(String name, NodeType type, Collection<String> parents) throws PMException {
        if (query().graph().nodeExists(name)) {
            throw new NodeNameExistsException(name);
        }

        // when creating a node the only loop that can occur is to itself
        if (parents.contains(name)) {
            throw new AssignmentCausesLoopException(name, name);
        }

        // object attributes, objects, and users need to be assigned to at least one node initially
        if (type != UA && parents.isEmpty()) {
            throw new DisconnectedNodeException(name, type);
        }

        // check assign inputs
        for (String p : parents) {
            if (name.equals(p)) {
                throw new AssignmentCausesLoopException(name, p);
            }

            Node parentNode = query().graph().getNode(p);
            Assignment.checkAssignment(type, parentNode.getType());
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
        Collection<String> children;
        try {
            children = query().graph().getChildren(name);
        } catch (NodeDoesNotExistException e) {
            // quietly return if the nodes already does not exist as this is the desired state
            return false;
        }

        if (!children.isEmpty()) {
            throw new NodeHasChildrenException(name);
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
     * @param child  The child node.
     * @param parent The parent node.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected boolean checkAssignInput(String child, String parent) throws PMException {
        // ignore if assignment already exists
        if (query().graph().getParents(child).contains(parent)) {
            return false;
        }

        // getting both nodes will check if they exist
        Node childNode = query().graph().getNode(child);
        Node parentNode = query().graph().getNode(parent);

        // check node types make a valid assignment relation
        Assignment.checkAssignment(childNode.getType(), parentNode.getType());

        // check the assignment won't create a loop
        checkAssignmentDoesNotCreateLoop(child, parent);

        return true;
    }

    /**
     * Check if both nodes exist. If the assignment does not exist an error does not occur but return false to indicate
     * to the caller that execution should not proceed.
     *
     * @param child  The child node.
     * @param parent The parent node.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected boolean checkDeassignInput(String child, String parent) throws PMException {
        if (!query().graph().nodeExists(child)) {
            throw new NodeDoesNotExistException(child);
        } else if (!query().graph().nodeExists(parent)) {
            throw new NodeDoesNotExistException(parent);
        }

        Collection<String> parents = query().graph().getParents(child);
        if (!parents.contains(parent)) {
            return false;
        }

        if (parents.size() == 1) {
            throw new DisconnectedNodeException(child, parent);
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
            System.out.println(resourceAccessRights.contains(ar));
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

    private String createNonPolicyClassNode(String name, NodeType type, Map<String, String> properties, Collection<String> parents)
            throws PMException {
        return runTx(() -> {
            checkCreateNodeInput(name, type, parents);

            createNodeInternal(name, type, properties);

            for (String parent : parents) {
                createAssignmentInternal(name, parent);
            }

            return name;
        });

    }
}
