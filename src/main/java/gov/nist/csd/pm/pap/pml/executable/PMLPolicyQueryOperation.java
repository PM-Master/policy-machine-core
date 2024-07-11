package gov.nist.csd.pm.pap.pml.executable;

import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.List;

public class PMLPolicyQueryOperation extends PMLPolicyExecutable {

    public PMLPolicyQueryOperation(String name, Type returnType,
                                   List<PMLRequiredCapability> capMap,
                                   PMLOperationExecutor executor) {
        super(name, returnType, capMap, executor);
    }
}
