package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.ObligationsModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.obligation.CreateObligationOp;
import gov.nist.csd.pm.pap.op.obligation.DeleteObligationOp;
import gov.nist.csd.pm.pap.op.obligation.UpdateObligationOp;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.Collection;
import java.util.List;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class ObligationsModificationAdjudicator implements ObligationsModification {
    private final UserContext userCtx;
    private final PAP pap;
    private final EventEmitter eventEmitter;

    public ObligationsModificationAdjudicator(UserContext userCtx, PAP pap, EventEmitter eventEmitter) {
        this.userCtx = userCtx;
        this.pap = pap;
        this.eventEmitter = eventEmitter;
    }

    @Override
    public void create(UserContext author, String name, Collection<Rule> rules) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.OBLIGATIONS_TARGET.nodeName(), CREATE_OBLIGATION);

        pap.modify().obligations().create(author, name, rules);

        eventEmitter.emitEvent(new EventContext(userCtx, new CreateObligationOp(author, name, rules)));
    }

    @Override
    public void update(UserContext author, String name, Collection<Rule> rules) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.OBLIGATIONS_TARGET.nodeName(), CREATE_OBLIGATION);

        pap.modify().obligations().update(author, name, rules);

        eventEmitter.emitEvent(new EventContext(userCtx, new UpdateObligationOp(author, name, rules)));
    }

    @Override
    public void delete(String name) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.OBLIGATIONS_TARGET.nodeName(), DELETE_OBLIGATION);

        Obligation obligation = pap.query().obligations().get(name);

        pap.modify().obligations().delete(name);

        eventEmitter.emitEvent(new EventContext(
                userCtx,
                new DeleteObligationOp(obligation.getAuthor(), obligation.getName(), obligation.getRules()))
        );
    }
}
