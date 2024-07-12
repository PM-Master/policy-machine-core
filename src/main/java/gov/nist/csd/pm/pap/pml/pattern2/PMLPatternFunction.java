package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.pml.function.PMLFunction;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

public class PMLPatternFunction extends PMLFunction {

    public PMLPatternFunction(String name, Type returnType,
                              Map<String, PMLRequiredCapability> capMap,
                              OperationExecutor<Value> executor) {
        super(name, returnType, capMap, executor);
    }
}
