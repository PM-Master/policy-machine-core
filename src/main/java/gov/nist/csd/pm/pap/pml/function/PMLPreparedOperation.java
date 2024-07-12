package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.PreparedOperation;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.HashMap;
import java.util.Map;

public class PMLPreparedOperation extends PreparedOperation<Value> {

    protected ExecutionContext ctx;

    public PMLPreparedOperation(Operation<Value> op, Map<String, Object> operands) {
        super(op, operands);
    }

    public PMLPreparedOperation withCtx(ExecutionContext ctx) {
        this.ctx = ctx;
        return this;
    }


}
