package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.PreparedOperation;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.HashMap;
import java.util.Map;

public abstract class PMLFunction extends Operation<Value> {

    protected ExecutionContext ctx;
    private Type returnType;
    private Map<String, PMLRequiredCapability> capMap;

    public PMLFunction(String name, Type returnType, Map<String, PMLRequiredCapability> capMap,
                         OperationExecutor<Value> executor) {
        super(name, new HashMap<>(capMap), executor);

        this.returnType = returnType;
        this.capMap = capMap;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Map<String, PMLRequiredCapability> getPMLCapMap() {
        return capMap;
    }

    @Override
    public PMLPreparedOperation withOperands(Map<String, Object> operands) {
        return new PMLPreparedOperation(this, operands);
    }
}
