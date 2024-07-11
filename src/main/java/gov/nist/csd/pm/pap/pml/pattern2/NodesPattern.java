package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.executable.PMLStatementExecutor;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.List;

public class NodesPattern extends PMLFunction {
    public NodesPattern(String opName, Type returnType,
                        List<PMLRequiredCapability> capMap,
                        PMLStatementExecutor executor) {
        super(opName, returnType, capMap, executor);
    }
}
