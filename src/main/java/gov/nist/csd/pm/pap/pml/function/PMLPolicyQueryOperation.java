package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.HashMap;
import java.util.Map;

public class PMLPolicyQueryOperation extends PMLPolicyFunction {

    public PMLPolicyQueryOperation(String name, Type returnType,
                                   Map<String, PMLRequiredCapability> capMap,
                                   PMLOperationExecutor executor) {
        super(name, returnType, new HashMap<>(capMap), executor);
    }
}
