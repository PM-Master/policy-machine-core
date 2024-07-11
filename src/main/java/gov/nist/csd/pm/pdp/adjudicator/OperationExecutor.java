package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.query.UserContext;

public class OperationExecutor {

    public void executeOpAndEmitEvent(UserContext userCtx, EventEmitter eventEmitter, Operation op, Object ... operands)
            throws PMException {
        op.canExecute(userCtx, operands);
        op.execute(operands);

        eventEmitter.emitEvent(op.toEventContext(userCtx, op));
    }

}
