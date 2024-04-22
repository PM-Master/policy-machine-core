package gov.nist.csd.pm.impl.neo4j.pdp;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.ObligationsReview;

import java.util.List;
import java.util.Map;

public class Neo4jObligationsReviewer implements ObligationsReview {
    @Override
    public List<Obligation> getObligationsWithAuthor(UserContext userCtx) throws PMException {
        throw new PMException("not yet implemented");
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventSubject(String subject) throws PMException {
        throw new PMException("not yet implemented");
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventTarget(String target) throws PMException {
        throw new PMException("not yet implemented");
    }

    @Override
    public List<Response> getMatchingEventResponses(EventContext eventCtx) throws PMException {
        throw new PMException("not yet implemented");
    }
}
