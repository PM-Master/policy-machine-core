package gov.nist.csd.pm.pap.pml.executable;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public class PMLStatementExecutor extends PMLOperationExecutor {

    private List<PMLStatement> statements;

    public PMLStatementExecutor(List<PMLStatement> statements) {
        super();
        this.statements = statements;
    }

    public PMLStatementExecutor() {
        this.statements = new ArrayList<>();
    }

    @Override
    public Value execute(PAP pap, List<Object> operands) throws PMException {
        if (executionContext == null) {
            throw new IllegalStateException("executionContext is null");
        }

        return executionContext.executeStatements(pap, statements);
    }
}
