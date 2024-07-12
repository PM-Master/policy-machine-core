package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.pml.statement.PMLStatement;

import java.util.List;
import java.util.Map;

public class PMLRoutine extends PMLOperation {

    public PMLRoutine(String opName, Map<String, PMLRequiredCapability> capMap, List<PMLStatement> stmts) {
        super(opName, capMap, stmts);
    }
}
