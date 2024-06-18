package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.exception.*;

import java.util.Collection;
import java.util.Map;

/**
 * NGAC graph methods.
 */
public interface GraphModification {

    /**
     * Set the resource access rights recognized in this policy.
     *
     * @param accessRightSet The operations to set as the resource access rights.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void setResourceAccessRights(AccessRightSet accessRightSet) throws PMException;

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
     * Create a new user attribute and assign it to the provided parents. User attributes must be assigned to at
     * least one parent node initially.
     *
     * @param name    the name of the user attribute
     * @param parents A list of parents to assign the new node to.
     *
     * @return the name of the user attribute.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    String createUserAttribute(String name, Map<String, String> properties, Collection<String> parents) throws PMException;

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
    String createObjectAttribute(String name, Map<String, String> properties, Collection<String> parents) throws PMException;

    /**
     * Create a new object and assign it to the provided parents. Objects must be assigned to at least one parent node
     * initially.
     *
     * @param name    The name of the object attribute
     * @param parents A list of 0 or more parents to assign the new node to.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    String createObject(String name, Map<String, String> properties, Collection<String> parents) throws PMException;

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
    String createUser(String name, Map<String, String> properties, Collection<String> parents) throws PMException;

    /**
     * Update the properties of the node with the given name. The given properties overwrite any existing properties.
     *
     * @param name       The name of the node to update.
     * @param properties The properties to give the node.
     * @throws PMBackendException If there is an error executing the command in the PIP.
     */
    void setNodeProperties(String name, Map<String, String> properties) throws PMException;

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

}
