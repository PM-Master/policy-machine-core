package gov.nist.csd.pm.pap.pml.executable;

import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;

public abstract class PMLOperationExecutor implements OperationExecutor<Value> {

    protected ExecutionContext executionContext;

    public PMLOperationExecutor() {
    }

    public PMLOperationExecutor withCtx(ExecutionContext executionContext) {
        this.executionContext = executionContext;
        return this;
    }
}
