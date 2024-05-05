package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.dag.DepthFirstGraphWalker;
import gov.nist.csd.pm.common.op.pattern.Pattern;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.common.graph.dag.Direction;
import gov.nist.csd.pm.common.graph.nodes.Node;
import gov.nist.csd.pm.common.graph.nodes.NodeType;
import gov.nist.csd.pm.common.graph.relationships.Assignment;
import gov.nist.csd.pm.common.graph.relationships.Association;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.exception.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static gov.nist.csd.pm.pdp.AdminAccessRights.*;
import static gov.nist.csd.pm.common.graph.nodes.NodeType.UA;

/**
 * NGAC graph methods.
 */
public interface Graph {

    /**
     * Set the resource access rights recognized in this policy.
     *
     * @param accessRightSet The operations to set as the resource access rights.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void setResourceAccessRights(AccessRightSet accessRightSet) throws PMException;

    /**
     * Get the resource access rights recognized by this policy.
     *
     * @return The resource access rights recognized by this policy.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    AccessRightSet getResourceAccessRights() throws PMException;

    /**
     * Create a policy class in the graph. This method should also create an object attribute that represents the
     * policy class in {@link AdminPolicyNode#POLICY_CLASS_TARGETS}. This object attribute can be used in
     * the future to create associations with the policy class itself which is not a supported relation in NGAC.
     * If the provided name equals {@link AdminPolicyNode#ADMIN_POLICY} then this method should also create the
     * {@link AdminPolicyNode#POLICY_CLASS_TARGETS} node and assign it to ADMIN_POLICY before creating
     * {@link AdminPolicyNode#ADMIN_POLICY_TARGET}
     *
     * @param name The name of the policy class.
     * @return The name of the policy class.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    String createPolicyClass(String name, Map<String, String> properties) throws PMException;

    /**
     * Create a new user attribute and assign it to the provided parent and optional additional parents.
     *
     * @param name    the name of the user attribute
     * @param parents A list of parents to assign the new node to.
     *
     * @return the name of the user attribute.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    String createUserAttribute(String name, Map<String, String> properties, List<String> parents) throws PMException;

    /**
     * Create a new object attribute and assign it to the provided parents. Object attributes must be assigned to at
     * least one parent node initially.
     *
     * @param name    The name of the object attribute
     * @param parents A list of 0 or more parents to assign the new node to.
     *
     * @return The name of the object attribute.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    String createObjectAttribute(String name, Map<String, String> properties, List<String> parents) throws PMException;

    /**
     * Create a new object and assign it to the provided parents. Objects must be assigned to at least one parent node
     * initially.
     *
     * @param name    The name of the object attribute
     * @param parents A list of 0 or more parents to assign the new node to.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    String createObject(String name, Map<String, String> properties, List<String> parents) throws PMException;

    /**
     * Create a new user and assign it to the provided parents. Users must be assigned to at least one parent node
     * initially.
     *
     * @param name    The name of the object attribute
     * @param parents A list of 0 or more parents to assign the new node to.
     *
     * @return The name of the object attribute.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    String createUser(String name, Map<String, String> properties, List<String> parents) throws PMException;

    /**
     * Update the properties of the node with the given name. The given properties overwrite any existing properties.
     *
     * @param name       The name of the node to update.
     * @param properties The properties to give the node.
     * @throws PMBackendException If there is an error executing the command in the PIP.
     */
    void setNodeProperties(String name, Map<String, String> properties) throws PMException;

    /**
     * Check if a node exists in the graph.
     *
     * @param name The name of the node to check for.
     * @return True if the node exists, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    boolean nodeExists(String name) throws PMException;

    /**
     * Get the Node object associated with the given name.
     *
     * @param name The name of the node to get.
     * @return The Node with the given name.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Node getNode(String name) throws PMException;

    /**
     * Search for nodes with the given type and/or properties. To return all nodes, use type=NodeType.ANY and properties=new HashMap<>().
     *
     * Supports wildcard property values i.e. {"prop1": "*"} which will match any nodes with the "prop1" property key.
     *
     * @param type       The type of nodes to search for. Use NodeType.ANY to search for any node type.
     * @param properties The properties of nodes to search for. An empty map will match all nodes.
     * @return The nodes that match the type and property criteria.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    List<String> search(NodeType type, Map<String, String> properties) throws PMException;

    /**
     * Get all policy class names.
     *
     * @return The names of all policy classes.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    List<String> getPolicyClasses() throws PMException;

    /**
     * Delete the node with the given name from the graph. If the node is a policy class this will also delete the
     * representative object attribute. An exception will be thrown if the node has any nodes assigned to it or if
     * the node is defined in a prohibition or an obligation event pattern. If the node does not exist, no exception
     * will be thrown as this is the desired state.
     *
     * @param name The name of the node to delete.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void deleteNode(String name) throws PMException;

    /**
     * Assign the child node to the parent node.
     *
     * @param child  The name of the child node.
     * @param parent The name of the parent node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void assign(String child, String parent) throws PMException;

    /**
     * Delete the assignment between the child and parent nodes.
     *
     * @param child  The name of the child node.
     * @param parent The name of the parent node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void deassign(String child, String parent) throws PMException;

    /**
     * Get the parents of the given node.
     *
     * @param node The node to get the parents of.
     * @return The names of the parents of the given node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    List<String> getParents(String node) throws PMException;

    /**
     * Get the children of the given node.
     *
     * @param node The node to get the children of.
     * @return The names of the children of the given node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    List<String> getChildren(String node) throws PMException;

    /**
     * Create an association between the user attribute and the target node with the provided access rights.
     * If an association already exists between these two nodes, overwrite the existing access rights with the ones
     * provided. Associations can only begin at a user attribute but can point to either an object or user attribute. If
     * either node does not exist or a provided access right is unknown to the policy an exception will be thrown.
     *
     * @param ua The name of the user attribute.
     * @param target The name of the target attribute.
     * @param accessRights The set of access rights to add to the association.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void associate(String ua, String target, AccessRightSet accessRights) throws PMException;

    /**
     * Delete the association between the user attribute and target node.  If either of the nodes does not exist an
     * exception will be thrown. If the association does not exist no exception will be thrown as this is the desired
     * state.
     *
     * @param ua The name of the user attribute.
     * @param target The name of the target attribute.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void dissociate(String ua, String target) throws PMException;

    /**
     * Get the associations in which the given user attribute is the source.
     *
     * @param ua The user attribute to get the associations for.
     * @return The associations in which the source of the relation is the given user attribute.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    List<Association> getAssociationsWithSource(String ua) throws PMException;

    /**
     * Get the associations in which the given node is the target.
     *
     * @param target The target attribute to get the associations for.
     * @return The associations in which the target of the relation is the given node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    List<Association> getAssociationsWithTarget(String target) throws PMException;

    /**
     * Check if a proposed assignment causes a loop.
     *
     * @param child  The child of the assignment.
     * @param parent The parent of the assignment.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkAssignmentDoesNotCreateLoop(String child, String parent) throws PMException {
        AtomicBoolean loop = new AtomicBoolean(false);

        new DepthFirstGraphWalker(this)
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
    default void checkSetResourceAccessRightsInput(AccessRightSet accessRightSet) throws PMException {
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
    default void checkCreatePolicyClassInput(String name) throws PMException {
        if (nodeExists(name)) {
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
    default void checkCreateNodeInput(String name, NodeType type, List<String> parents) throws PMException {
        if (nodeExists(name)) {
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

            Node parentNode = getNode(p);
            Assignment.checkAssignment(type, parentNode.getType());
        }
    }

    /**
     * Check if the given nodes exists.
     *
     * @param name The name of the node to check.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkSetNodePropertiesInput(String name) throws PMException {
        if (!nodeExists(name)) {
            throw new NodeDoesNotExistException(name);
        }
    }

    /**
     * Check if the node exists.
     *
     * @param name The node to check.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkGetNodeInput(String name) throws PMException {
        if (!nodeExists(name)) {
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
     * @param prohibitions The ProhibitionStore used to check if the node is referenced in a prohibition.
     * @param obligations  The ObligationStore used to check if the node is referenced in an obligation event
     *                          pattern.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default boolean checkDeleteNodeInput(String name, Prohibitions prohibitions, Obligations obligations) throws PMException {
        List<String> children;
        try {
            children = getChildren(name);
        } catch (NodeDoesNotExistException e) {
            // quietly return if the nodes already does not exist as this is the desired state
            return false;
        }

        if (!children.isEmpty()) {
            throw new NodeHasChildrenException(name);
        }

        checkIfNodeInProhibition(name, prohibitions);
        checkIfNodeInObligation(name, obligations);

        return true;
    }

    /**
     * Helper method to check if a given node is referenced in any prohibitions. The default implementation loads all
     * prohibitions into memory and then searches through each one.
     *
     * @param name             The node to check for.
     * @param prohibitionStore The ProhibitionStore used to get the prohibitions.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkIfNodeInProhibition(String name, Prohibitions prohibitionStore) throws PMException {
        Map<String, List<Prohibition>> allProhibitions = prohibitionStore.getAll();
        for (List<Prohibition> subjPros : allProhibitions.values()) {
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
     * @param obligationsStore The ObligationStore used to get the obligations.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkIfNodeInObligation(String name, Obligations obligationsStore) throws PMException {
        List<Obligation> obligations = obligationsStore.getAll();
        for (Obligation obligation : obligations) {
            // if the node is the author of the obligation or referenced in any rules throw an exception
            if (obligation.getAuthor().getUser().equals(name)) {
                throw new NodeReferencedInObligationException(name, obligation.getName());
            }
        }
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
    default boolean checkAssignInput(String child, String parent) throws PMException {
        // ignore if assignment already exists
        if (getParents(child).contains(parent)) {
            return false;
        }

        // getting both nodes will check if they exist
        Node childNode = getNode(child);
        Node parentNode = getNode(parent);

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
    default boolean checkDeassignInput(String child, String parent) throws PMException {
        if (!nodeExists(child)) {
            throw new NodeDoesNotExistException(child);
        } else if (!nodeExists(parent)) {
            throw new NodeDoesNotExistException(parent);
        }

        List<String> parents = getParents(child);
        if (!parents.contains(parent)) {
            return false;
        }

        if (parents.size() == 1) {
            throw new DisconnectedNodeException(child, parent);
        }

        return true;
    }

    /**
     * Check that the provided node exists.
     *
     * @param node The node to check.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkGetParentsInput(String node) throws PMException {
        if (!nodeExists(node)) {
            throw new NodeDoesNotExistException(node);
        }
    }

    /**
     * Check that the provided node exists.
     *
     * @param node The node to check.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkGetChildrenInput(String node) throws PMException {
        if (!nodeExists(node)) {
            throw new NodeDoesNotExistException(node);
        }
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
    default void checkAssociateInput(String ua, String target, AccessRightSet accessRights) throws PMException {
        Node uaNode = getNode(ua);
        Node targetNode = getNode(target);

        // check the access rights are valid
        checkAccessRightsValid(getResourceAccessRights(), accessRights);

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
    default boolean checkDissociateInput(String ua, String target) throws PMException {
        if (!nodeExists(ua)) {
            throw new NodeDoesNotExistException(ua);
        } else if (!nodeExists(target)) {
            throw new NodeDoesNotExistException(target);
        }

        List<Association> associations = getAssociationsWithSource(ua);
        for (Association a : associations) {
            if (a.getSource().equals(ua) && a.getTarget().equals(target)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check the source node to get associations for.
     *
     * @param ua The source node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkGetAssociationsWithSourceInput(String ua) throws PMException {
        if (!nodeExists(ua)) {
            throw new NodeDoesNotExistException(ua);
        }
    }

    /**
     * Check the target node to get associations for.
     *
     * @param target The target node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkGetAssociationsWithTargetInput(String target) throws PMException {
        if (!nodeExists(target)) {
            throw new NodeDoesNotExistException(target);
        }
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
}
