package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.pap.query.ObligationsQuery;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.GET_OBLIGATION;

public class ObligationsQueryAdjudicator implements ObligationsQuery {

    private final UserContext userCtx;
    private final PAP pap;

    public ObligationsQueryAdjudicator(UserContext userCtx, PAP pap) {
        this.userCtx = userCtx;
        this.pap = pap;
    }

    @Override
    public Collection<Obligation> getAll() throws PMException {
        Collection<Obligation> obligations = pap.query().obligations().getAll();
        obligations.removeIf(obligation -> {
            try {
                for (Rule rule : obligation.getRules()) {
                    checkRule(rule);
                }
                return false;
            } catch (PMException e) {
                return true;
            }
        });

        return obligations;
    }

    @Override
    public boolean exists(String name) throws PMException {
        boolean exists = pap.query().obligations().exists(name);
        if (!exists) {
            return false;
        }

        try {
            get(name);
        } catch (UnauthorizedException e) {
            return false;
        }

        return true;
    }

    @Override
    public Obligation get(String name) throws PMException {
        Obligation obligation = pap.query().obligations().get(name);
        for (Rule rule : obligation.getRules()) {
            checkRule(rule);
        }

        return obligation;
    }

    @Override
    public Collection<Obligation> getObligationsWithAuthor(UserContext userCtx) throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);

        return pap.query().obligations().getObligationsWithAuthor(userCtx);
    }

    private void checkRule(Rule rule) throws PMException {
        EventPattern eventPattern = rule.getEventPattern();

        // check subject
        PrivilegeChecker.checkPattern(pap, userCtx, eventPattern.getSubjectPattern(), GET_OBLIGATION);

        // cannot check operation as it is not a node

        // check operands
        for (Pattern operandPattern : eventPattern.getOperandPatterns()) {
            PrivilegeChecker.checkPattern(pap, userCtx, operandPattern, GET_OBLIGATION);
        }
    }
}
