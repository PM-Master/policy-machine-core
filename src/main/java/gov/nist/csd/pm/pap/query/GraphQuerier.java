package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.pap.exception.NodeDoesNotExistException;

import java.util.Collection;

public abstract class GraphQuerier implements GraphQuery{

    protected abstract Node getNodeInternal(String name) throws PMException;
    protected abstract Collection<String> getDescendantsInternal(String name) throws PMException;
    protected abstract Collection<String> getAscendantsInternal(String name) throws PMException;
    protected abstract Collection<Association> getAssociationsWithSourceInternal(String ua) throws PMException;
    protected abstract Collection<Association> getAssociationsWithTargetInternal(String target) throws PMException;
    protected abstract Collection<String> getAttributeContainersInternal(String node) throws PMException;
    protected abstract Collection<String> getPolicyClassContainersInternal(String node) throws PMException;
    protected abstract boolean isAscendantInternal(String node, String container) throws PMException;

    @Override
    public Node getNode(String name) throws PMException {
        checkNodeExists(name);
        return getNodeInternal(name);
    }

    @Override
    public Collection<String> getAdjacentDescendants(String node) throws PMException {
        checkNodeExists(node);
        return getDescendantsInternal(node);
    }

    @Override
    public Collection<String> getAdjacentAscendants(String node) throws PMException {
        checkNodeExists(node);
        return getAscendantsInternal(node);
    }

    @Override
    public Collection<Association> getAssociationsWithSource(String ua) throws PMException {
        checkNodeExists(ua);
        return getAssociationsWithSourceInternal(ua);
    }

    @Override
    public Collection<Association> getAssociationsWithTarget(String target) throws PMException {
        checkNodeExists(target);
        return getAssociationsWithTargetInternal(target);
    }

    @Override
    public Collection<String> getAttributeDescendants(String node) throws PMException {
        checkNodeExists(node);
        return getAttributeContainersInternal(node);
    }

    @Override
    public Collection<String> getPolicyClassDescendants(String node) throws PMException {
        checkNodeExists(node);
        return getPolicyClassContainersInternal(node);
    }

    @Override
    public boolean isAscendant(String ascendant, String descendant) throws PMException {
        checkNodeExists(ascendant);
        checkNodeExists(descendant);
        return isAscendantInternal(ascendant, descendant);
    }

    /**
     * Check that the given nodes exists.
     * @param node The node to check.
     */
    protected void checkNodeExists(String node) throws PMException {
        if (!nodeExists(node)) {
            throw new NodeDoesNotExistException(node);
        }
    }
}
