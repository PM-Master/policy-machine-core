package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.scope.ExecuteGlobalScope;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.value.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PMLExecutor extends PMLCompiler {

    public void compileAndExecutePML(PolicyPoint pap, UserContext author, String input) throws PMException {
        // compile the PML into statements
        CompiledPML compiledPML = compilePML(input);

        // evaluate the constants and functions from the compiled PML
        Map<String, Value> persistedConstants = evaluateConstantStmts(pap, compiledPML.constants());
        persistedConstants.putAll(constants);

        // add the constants and functions to the persisted scope
        // build a global scope from the policy
        GlobalScope<Value, FunctionDefinitionStatement> globalScope = new ExecuteGlobalScope()
                .withProvidedConstants(persistedConstants)
                .withProvidedFunctions(functions);

        // execute other statements
        ExecutionContext ctx = new ExecutionContext(author, new Scope<>(globalScope));

        for (PMLStatement pmlStatement : compiledPML.stmts()) {
            pmlStatement.execute(ctx, pap);
        }
    }

    private static Map<String, Value> evaluateConstantStmts(PolicyPoint pap, Map<String, Expression> constants)
            throws PMException {
        Map<String, Value> constantsMap = new HashMap<>();

        // create empty exec ctx for constant value evaluation as all constants are literals
        ExecutionContext ctx = new ExecutionContext(new UserContext(), new Scope<>(new ExecuteGlobalScope()));

        for (Map.Entry<String, Expression> e : constants.entrySet()) {
            constantsMap.put(e.getKey(), e.getValue().execute(ctx, pap));
        }

        return constantsMap;
    }

    public static Value executeStatementBlock(ExecutionContext executionCtx, PolicyPoint pap, List<PMLStatement> statements) throws PMException {
        for (PMLStatement statement : statements) {
            Value value = statement.execute(executionCtx, pap);
            if (value instanceof ReturnValue || value instanceof BreakValue || value instanceof ContinueValue) {
                return value;
            }
        }

        return new VoidValue();
    }
}
