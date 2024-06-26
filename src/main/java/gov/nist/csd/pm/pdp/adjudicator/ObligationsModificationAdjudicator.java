package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.ObligationsModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.op.obligation.CreateObligationOp;
import gov.nist.csd.pm.pap.op.obligation.DeleteObligationOp;
import gov.nist.csd.pm.pap.op.obligation.UpdateObligationOp;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class ObligationsModificationAdjudicator extends OperationExecutor implements ObligationsModification {
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
        CreateObligationOp op = new CreateObligationOp(author, name, rules);

        executeOpAndEmitEvent(pap, userCtx, op, eventEmitter);
    }

    @Override
    public void update(UserContext author, String name, Collection<Rule> rules) throws PMException {
        UpdateObligationOp op = new UpdateObligationOp(author, name, rules);

        executeOpAndEmitEvent(pap, userCtx, op, eventEmitter);
    }

    @Override
    public void delete(String name) throws PMException {
        Obligation obligation = pap.query().obligations().get(name);

        DeleteObligationOp op = new DeleteObligationOp(
                obligation.getAuthor(),
                obligation.getName(),
                obligation.getRules()
        );

        executeOpAndEmitEvent(pap, userCtx, op, eventEmitter);
    }
}
