package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.List;
import java.util.Map;

public interface ObligationsReview {

    List<Obligation> getObligationsWithAuthor(UserContext userCtx) throws PMException;
    Map<String, List<Rule>> getRulesWithEventSubject(String subject) throws PMException;
    Map<String, List<Rule>> getRulesWithEventTarget(String target) throws PMException;
    List<Response> getMatchingEventResponses(EventContext eventCtx) throws PMException;

}
