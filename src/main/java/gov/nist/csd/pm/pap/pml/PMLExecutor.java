package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;
import gov.nist.csd.pm.pap.pml.statement.operation.OperationStatement;
import gov.nist.csd.pm.pap.pml.value.*;

import java.util.List;

public class PMLExecutor extends PMLCompiler {

    public Value executeStatement(ExecutionContext ctx, PAP pap, PMLStatement stmt) throws PMException {
        return stmt.execute(ctx, pap);
    }

    public Value executeStatements(ExecutionContext executionCtx, PAP pap, List<PMLStatement> statements) throws PMException {
        for (PMLStatement statement : statements) {
            Value value = executeStatement(executionCtx, pap, statement);
            if (value instanceof ReturnValue || value instanceof BreakValue || value instanceof ContinueValue) {
                return value;
            }
        }

        return new VoidValue();
    }

   /* @Override
    public PMLExecutor withFunctions(Collection<FunctionDefinitionStatement> funcs) {
        super.withFunctions(funcs);

        return this;
    }

    public void compileAndExecutePML(PolicyPoint policyPoint, UserContext author, String input) throws PMException {
        // compile the PML into statements
        CompiledPML compiledPML = compilePML(input);

        // add the constants and functions to the persisted scope
        // build a global scope from the policy
        GlobalScope<Value, FunctionDefinitionStatement> globalScope = new ExecuteGlobalScope()
                .withFunctions(functions);

        // execute other statements
        ExecutionContext ctx = new ExecutionContext(author, new Scope<>(globalScope));

        for (PMLStatement pmlStatement : compiledPML.stmts()) {
            if isOPeration call canExecute
            pmlStatement.execute(ctx, policyPoint);
        }
    }

    private Map<String, Value> evaluateConstantStmts(PolicyPoint policyPoint, Map<String, Expression> constants)
            throws PMException {
        Map<String, Value> constantsMap = new HashMap<>();

        // create empty exec ctx for constant value evaluation as all constants are literals
        ExecutionContext ctx = new ExecutionContext(new UserContext(), new Scope<>(new ExecuteGlobalScope()));

        for (Map.Entry<String, Expression> e : constants.entrySet()) {
            constantsMap.put(e.getKey(), e.getValue().execute(ctx, policyPoint));
        }

        return constantsMap;
    }*/
}
