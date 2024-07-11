package gov.nist.csd.pm.pap.pml.executable;

import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public abstract class PMLFunction extends PMLExecutable{

    public PMLFunction(String name, Type returnType, List<PMLRequiredCapability> capMap,
                       OperationExecutor<Value> executor) {
        super(name, returnType, new ArrayList<>(capMap), executor);
    }
}
