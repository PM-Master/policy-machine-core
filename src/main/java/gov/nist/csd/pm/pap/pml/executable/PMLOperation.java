package gov.nist.csd.pm.pap.pml.executable;

import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.List;

public class PMLOperation extends PMLPolicyExecutable {

    private List<PMLStatement> statements;

    public PMLOperation(String opName, List<PMLRequiredCapability> capMap, List<PMLStatement> stmts) {
        super(opName, Type.voidType(), capMap, new PMLStatementExecutor(stmts));

        this.statements = stmts;
    }

    public List<PMLStatement> getStatements() {
        return statements;
    }

}
