package gov.nist.csd.pm.pap.pml.executable;

import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public abstract class PMLExecutable {

    private String name;
    private Type returnType;
    private List<RequiredCapability> capMap;
    private OperationExecutor<Value> executor;

    public PMLExecutable(String name, Type returnType, List<RequiredCapability> capMap,
                         OperationExecutor<Value> executor) {
        this.name = name;
        this.capMap = capMap;
        this.returnType = returnType;
        this.executor = executor;
    }

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<RequiredCapability> getCapMap() {
        return capMap;
    }

    public OperationExecutor<Value> getExecutor() {
        return executor;
    }
}
