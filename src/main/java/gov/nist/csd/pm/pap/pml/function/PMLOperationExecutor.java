package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

public abstract class PMLOperationExecutor implements OperationExecutor<Value> {

    private ExecutionContext ctx;

    public PMLOperationExecutor withCtx(ExecutionContext ctx) {
        this.ctx = ctx;

        return this;
    }

    public ExecutionContext getCtx() {
        return ctx;
    }
}
