package gov.nist.csd.pm.impl.neo4j.pdp;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.ProhibitionsReview;

import java.util.List;

public class Neo4jProhibitionReviewer implements ProhibitionsReview {
    @Override
    public List<Prohibition> getInheritedProhibitionsFor(String subject) throws PMException {
        throw new PMException("not yet implemented");
    }

    @Override
    public List<Prohibition> getProhibitionsWithContainer(String container) throws PMException {
        throw new PMException("not yet implemented");
    }
}
