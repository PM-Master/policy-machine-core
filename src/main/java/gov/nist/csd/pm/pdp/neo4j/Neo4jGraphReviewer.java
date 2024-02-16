package gov.nist.csd.pm.pdp.neo4j;

import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.review.GraphReview;

import java.util.List;

public class Neo4jGraphReviewer implements GraphReview {
    @Override
    public List<String> getAttributeContainers(String node) throws PMException {
        throw new PMException("not yet implemented");
    }

    @Override
    public List<String> getPolicyClassContainers(String node) throws PMException {
        throw new PMException("not yet implemented");
    }

    @Override
    public boolean isContained(String subject, String container) throws PMException {
        throw new PMException("not yet implemented");
    }
}
