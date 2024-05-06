package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorObligationsQuery;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.query.ObligationsQuery;

import java.util.List;
import java.util.Map;

public class PDPObligationsQuery implements ObligationsQuery {

    private final AdjudicatorObligationsQuery adjudicator;
    private final ObligationsQuery obligationsQuery;

    public PDPObligationsQuery(AdjudicatorObligationsQuery adjudicator, ObligationsQuery obligationsQuery) {
        this.adjudicator = adjudicator;
        this.obligationsQuery = obligationsQuery;
    }

    @Override
    public List<Obligation> getObligationsWithAuthor(UserContext userCtx) throws PMException {
        adjudicator.getObligationsWithAuthor(userCtx);
        return obligationsQuery.getObligationsWithAuthor(userCtx);
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventSubject(String subject) throws PMException {
        adjudicator.getRulesWithEventSubject(subject);
        return obligationsQuery.getRulesWithEventSubject(subject);
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventOperation(String operation) throws PMException {
        return Map.of();//TODO
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventOperand(String target) throws PMException {
        adjudicator.getRulesWithEventOperand(target);
        return obligationsQuery.getRulesWithEventOperand(target);
    }

    @Override
    public List<Response> getMatchingEventResponses(EventContext eventCtx) throws PMException {
        adjudicator.getMatchingEventResponses(eventCtx);
        return obligationsQuery.getMatchingEventResponses(eventCtx);
    }
}
