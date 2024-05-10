package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.query.ObligationsQuery;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.AdminPolicyNode.ADMIN_POLICY_TARGET;

public class AdjudicatorObligationsQuery implements ObligationsQuery {

    private final UserContext userCtx;
    private final PrivilegeChecker privilegeChecker;

    public AdjudicatorObligationsQuery(UserContext userCtx, PrivilegeChecker privilegeChecker) {
        this.userCtx = userCtx;
        this.privilegeChecker = privilegeChecker;
    }

    @Override
    public List<Obligation> getObligationsWithAuthor(UserContext userCtx) throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventSubject(String subject) throws PMException {
        privilegeChecker.check(this.userCtx, subject, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventOperation(String operation) throws PMException {
        return Map.of();//TODO

    }

    @Override
    public Map<String, List<Rule>> getRulesWithEventOperand(String target) throws PMException {
        privilegeChecker.check(this.userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public List<Response> getMatchingEventResponses(EventContext eventCtx) throws PMException {
        privilegeChecker.check(this.userCtx, ADMIN_POLICY_TARGET.nodeName(), AdminAccessRights.REVIEW_POLICY);

        return null;
    }
}
