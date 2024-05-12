package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.pap.exception.NodeDoesNotExistException;

import java.util.Collection;

public abstract class GraphQuerier implements GraphQuery{

    protected abstract Node getNodeInternal(String name) throws PMException;
    protected abstract Collection<String> getParentsInternal(String name) throws PMException;
    protected abstract Collection<String> getChildrenInternal(String name) throws PMException;
    protected abstract Collection<Association> getAssociationsWithSourceInternal(String ua) throws PMException;
    protected abstract Collection<Association> getAssociationsWithTargetInternal(String target) throws PMException;
    protected abstract Collection<String> getAttributeContainersInternal(String node) throws PMException;
    protected abstract Collection<String> getPolicyClassContainersInternal(String node) throws PMException;
    protected abstract boolean isContainedInternal(String node, String container) throws PMException;

    @Override
    public Node getNode(String name) throws PMException {
        checkNodeExists(name);
        return getNodeInternal(name);
    }

    @Override
    public Collection<String> getParents(String node) throws PMException {
        checkNodeExists(node);
        return getParentsInternal(node);
    }

    @Override
    public Collection<String> getChildren(String node) throws PMException {
        checkNodeExists(node);
        return getChildrenInternal(node);
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
    public Collection<String> getAttributeContainers(String node) throws PMException {
        checkNodeExists(node);
        return getAttributeContainersInternal(node);
    }

    @Override
    public Collection<String> getPolicyClassContainers(String node) throws PMException {
        checkNodeExists(node);
        return getPolicyClassContainersInternal(node);
    }

    @Override
    public boolean isContained(String subject, String container) throws PMException {
        checkNodeExists(subject);
        checkNodeExists(container);
        return isContainedInternal(subject, container);
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
