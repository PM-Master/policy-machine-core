package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;

public class PDPPMLExecutor extends PMLExecutor {

    private UserContext userCtx;

    public PDPPMLExecutor(UserContext userCtx) {
        this.userCtx = userCtx;
    }

    @Override
    public Object executeStatements(ExecutionContext executionCtx, PAP pap, List<PMLStatementSerializer<?>> statements)
            throws PMException {
        return super.executeStatements(executionCtx, pap, statements);
    }

    @Override
    public Value executeStatement(ExecutionContext ctx, PolicyPoint policy, PMLStatementSerializer stmt) throws PMException {
        return super.executeStatement(ctx, policy, stmt);
    }

    @Override
    public Value executeStatement(ExecutionContext ctx, PAP pap, PMLStatementSerializer stmt) throws PMException {
        stmt.canExecute(pap, userCtx);

        return super.executeStatement(ctx, pap, stmt);
    }
}
