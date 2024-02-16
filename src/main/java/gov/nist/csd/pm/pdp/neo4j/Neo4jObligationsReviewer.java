package gov.nist.csd.pm.pdp.neo4j;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.UserContext;
import gov.nist.csd.pm.policy.model.obligation.Obligation;
import gov.nist.csd.pm.policy.model.obligation.Response;
import gov.nist.csd.pm.policy.model.obligation.Rule;
import gov.nist.csd.pm.policy.review.ObligationsReview;

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
