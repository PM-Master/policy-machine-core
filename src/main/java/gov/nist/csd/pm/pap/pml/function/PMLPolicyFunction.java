package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

public class PMLPolicyFunction extends PMLFunction {

    public PMLPolicyFunction(String name, Type returnType,
                             Map<String, RequiredCapability> capMap,
                             OperationExecutor<Value> executor) {
        super(name, returnType, capMap, executor);
        this.executor = executor;
    }
}
