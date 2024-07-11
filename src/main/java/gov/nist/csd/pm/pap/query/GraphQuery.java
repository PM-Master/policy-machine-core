package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;

import java.util.Collection;
import java.util.Map;

public interface GraphQuery {

    /**
     * Get the resource access rights recognized by this policy.
     *
     * @return The resource access rights recognized by this policy.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    AccessRightSet mode to operations getResourceAccessRights() throws PMException;

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
     * <p>
     * Supports wildcard property values i.e. {"prop1": "*"} which will match any nodes with the "prop1" property key.
     *
     * @param type       The type of nodes to search for. Use NodeType.ANY to search for any node type.
     * @param properties The properties of nodes to search for. An empty map will match all nodes.
     * @return The nodes that match the type and property criteria.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<String> search(NodeType type, Map<String, String> properties) throws PMException;

    /**
     * Get all policy class names.
     *
     * @return The names of all policy classes.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<String> getPolicyClasses() throws PMException;

    /**
     * Get the adjacent descendants of the given node.
     *
     * @param node The node to get the descendants of.
     * @return The names of the descendants of the given node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<String> getAdjacentDescendants(String node) throws PMException;

    /**
     * Get the adjacent ascendants of the given node.
     *
     * @param node The node to get the ascendants of.
     * @return The names of the ascendants of the given node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<String> getAdjacentAscendants(String node) throws PMException;

    /**
     * Get the associations in which the given user attribute is the source.
     *
     * @param ua The user attribute to get the associations for.
     * @return The associations in which the source of the relation is the given user attribute.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<Association> getAssociationsWithSource(String ua) throws PMException;

    /**
     * Get the associations in which the given node is the target.
     *
     * @param target The target attribute to get the associations for.
     * @return The associations in which the target of the relation is the given node.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<Association> getAssociationsWithTarget(String target) throws PMException;


    /**
     * Get the descendants of the given node that are attributes.
     *
     * @param node The node to get the attribute descendants of.
     * @return A Collection of attribute names.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<String> getAttributeDescendants(String node) throws PMException;

    /**
     * Get the descendants of the given node that are policy classes.
     *
     * @param node The node to get the policy class descendants of.
     * @return A Collection of policy class names.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<String> getPolicyClassDescendants(String node) throws PMException;

    /**
     * Return true if the node is an ascendant of the container.
     *
     * @param ascendant The node.
     * @param descendant The container.
     * @return True if the node is an ascendant of the container.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    boolean isAscendant(String ascendant, String descendant) throws PMException;

}
