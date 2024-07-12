package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public Value execute(PAP pap, Map<String, Object> operands) throws PMException {
        ExecutionContext ctx = getCtx();
        if (ctx == null) {
            throw new IllegalStateException("executionContext is null");
        }

        return ctx.executeStatements(pap, statements);
    }
}
