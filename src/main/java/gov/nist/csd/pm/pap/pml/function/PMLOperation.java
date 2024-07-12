package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PMLOperation extends PMLPolicyFunction {

    private List<PMLStatement> statements;
    private Map<String, PMLRequiredCapability> capMap;

    public PMLOperation(String opName, Map<String, PMLRequiredCapability> capMap, List<PMLStatement> stmts) {
        super(opName, Type.voidType(), new HashMap<>(capMap), new PMLStatementExecutor(stmts));

        this.statements = stmts;
        this.capMap = capMap;
    }

    public List<PMLStatement> getStatements() {
        return statements;
    }

    public Map<String, PMLRequiredCapability> getPmlCapMap() {
        return capMap;
    }
}
