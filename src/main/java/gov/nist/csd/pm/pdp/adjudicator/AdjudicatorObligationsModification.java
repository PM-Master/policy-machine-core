package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.ObligationsModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.List;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class AdjudicatorObligationsModification implements ObligationsModification {
    private final UserContext userCtx;
    private final PAP pap;
    private final PrivilegeChecker privilegeChecker;

    public AdjudicatorObligationsModification(UserContext userCtx, PAP pap, PrivilegeChecker privilegeChecker) {
        this.userCtx = userCtx;
        this.pap = pap;
        this.privilegeChecker = privilegeChecker;
    }

    @Override
    public void create(UserContext author, String name, Rule... rules) throws PMException {
        privilegeChecker.check(userCtx, AdminPolicyNode.OBLIGATIONS_TARGET.nodeName(), CREATE_OBLIGATION);

    }

    @Override
    public void update(UserContext author, String name, Rule... rules) throws PMException {
        create(author, name, rules);
    }

    @Override
    public void delete(String name) throws PMException {
        privilegeChecker.check(userCtx, AdminPolicyNode.OBLIGATIONS_TARGET.nodeName(), DELETE_OBLIGATION);
    }

    @Override
    public List<Obligation> getAll() throws PMException {
        List<Obligation> obligations = pap.policy().obligations().getAll();
       /* obligations.removeIf(obligation -> {
            try {
                for (Rule rule : obligation.getRules()) {
                    // TODO
                    Subject subject = rule.getEventPattern().getSubject();
                    checkSubject(subject, GET_OBLIGATION);

                    Target target = rule.getEventPattern().getTarget();
                    checkTarget(target, GET_OBLIGATION);
                }
                return false;
            } catch (PMException e) {
                return true;
            }
        });*/

        return obligations;
    }

    @Override
    public boolean exists(String name) throws PMException {
        boolean exists = pap.policy().obligations().exists(name);
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
        Obligation obligation = pap.policy().obligations().get(name);
        for (Rule rule : obligation.getRules()) {
            // TODO
            /*Subject subject = rule.getEventPattern().getSubject();
            checkSubject(subject, GET_OBLIGATION);

            Target target = rule.getEventPattern().getTarget();
            checkTarget(target, GET_OBLIGATION);*/
        }

        return obligation;
    }
}
