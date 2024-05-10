package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorGraphQuery;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.GraphQuery;

import java.util.List;
import java.util.Map;

public class PDPGraphQuery implements GraphQuery {

    private final AdjudicatorGraphQuery adjudicator;
    private final GraphQuery graphQuery;

    public PDPGraphQuery(AdjudicatorGraphQuery adjudicator, GraphQuery graphQuery) {
        this.adjudicator = adjudicator;
        this.graphQuery = graphQuery;
    }

    @Override
    public AccessRightSet getResourceAccessRights() throws PMException {
        return adjudicator.getResourceAccessRights();
    }

    @Override
    public boolean nodeExists(String name) throws PMException {
        return adjudicator.nodeExists(name);
    }

    @Override
    public Node getNode(String name) throws PMException {
        return adjudicator.getNode(name);
    }

    @Override
    public List<String> search(NodeType type, Map<String, String> properties) throws PMException {
        return adjudicator.search(type, properties);
    }

    @Override
    public List<String> getPolicyClasses() throws PMException {
        return adjudicator.getPolicyClasses();
    }

    @Override
    public List<String> getChildren(String node) throws PMException {
        return adjudicator.getChildren(node);
    }

    @Override
    public List<String> getParents(String node) throws PMException {
        return adjudicator.getParents(node);
    }

    @Override
    public List<Association> getAssociationsWithSource(String ua) throws PMException {
        return adjudicator.getAssociationsWithSource(ua);
    }

    @Override
    public List<Association> getAssociationsWithTarget(String target) throws PMException {
        return adjudicator.getAssociationsWithTarget(target);
    }

    @Override
    public List<String> getAttributeContainers(String node) throws PMException {
        adjudicator.getAttributeContainers(node);
        return graphQuery.getAttributeContainers(node);
    }

    @Override
    public List<String> getPolicyClassContainers(String node) throws PMException {
        adjudicator.getPolicyClassContainers(node);
        return graphQuery.getPolicyClassContainers(node);
    }

    @Override
    public boolean isContained(String subject, String container) throws PMException {
        adjudicator.isContained(subject, container);
        return graphQuery.isContained(subject, container);
    }
}
