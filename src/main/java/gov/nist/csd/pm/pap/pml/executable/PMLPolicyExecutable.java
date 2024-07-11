package gov.nist.csd.pm.pap.pml.executable;

import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.ArrayList;
import java.util.List;

public class PMLPolicyExecutable extends PMLExecutable {

    private PMLOperationExecutor executor;

    public PMLPolicyExecutable(String name, Type returnType,
                               List<PMLRequiredCapability> capMap,
                               PMLOperationExecutor executor) {
        super(name, returnType, new ArrayList<>(capMap), executor);
        this.executor = executor;
    }

    public PMLExecutable withCtx(ExecutionContext ctx) {
        // provide the PMLExecutable the execution context to be used during its execute function
        this.executor.withCtx(ctx);

        return this;
    }
}
