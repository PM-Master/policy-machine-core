package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorGraphQuery;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.GraphQuery;

import java.util.List;

public class PDPGraphQuery implements GraphQuery {

    private final AdjudicatorGraphQuery adjudicator;
    private final GraphQuery graphQuery;

    public PDPGraphQuery(AdjudicatorGraphQuery adjudicator, GraphQuery graphQuery) {
        this.adjudicator = adjudicator;
        this.graphQuery = graphQuery;
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
