package gov.nist.csd.pm.pap.pml.executable;

import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;

import java.util.List;

public class PMLRoutine extends PMLOperation {

    public PMLRoutine(String opName, List<PMLRequiredCapability> capMap, List<PMLStatement> stmts) {
        super(opName, capMap, stmts);
    }
}
