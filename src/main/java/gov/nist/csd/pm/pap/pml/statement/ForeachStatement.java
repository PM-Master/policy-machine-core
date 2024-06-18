package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.value.*;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.pml.PMLExecutor.executeStatementBlock;

public class ForeachStatement extends PMLStatement {

    private final String varName;
    private final String valueVarName;
    private final Expression iter;
    private final List<PMLStatement> statements;

    public ForeachStatement(String varName, String valueVarName, Expression iter, List<PMLStatement> statements) {
        this.varName = varName;
        this.valueVarName = valueVarName;
        this.iter = iter;
        this.statements = statements;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        if (statements.isEmpty()) {
            return new VoidValue();
        }

        Value iterValue = iter.execute(ctx, policy);
        if (iterValue instanceof ArrayValue arrayValue) {
            return executeArrayIterator(arrayValue, ctx, policy);
        } else if (iterValue instanceof MapValue mapValue) {
            return executeMapIterator(mapValue, ctx, policy);
        }

        return new VoidValue();
    }

    private Value executeArrayIterator(ArrayValue iterValue, ExecutionContext ctx, PolicyPoint pap) throws PMException{
        for (Value v : iterValue.getValue()) {
            ExecutionContext localExecutionCtx;
            try {
                localExecutionCtx = ctx.copy();
            } catch (PMLScopeException e) {
                throw new RuntimeException(e);
            }

            localExecutionCtx.scope().addVariable(varName, v);

            Value value = executeStatementBlock(localExecutionCtx, pap, statements);

            if (value instanceof BreakValue) {
                break;
            } else if (value instanceof ReturnValue) {
                return value;
            }

            ctx.scope().local().overwriteFromLocalScope(localExecutionCtx.scope().local());
        }
        return new VoidValue();
    }

    private Value executeMapIterator(MapValue iterValue, ExecutionContext ctx, PolicyPoint pap) throws PMException{
        for (Value key : iterValue.getValue().keySet()) {
            ExecutionContext localExecutionCtx;
            try {
                localExecutionCtx = ctx.copy();
            } catch (PMLScopeException e) {
                throw new RuntimeException(e);
            }

            Value mapValue = iterValue.getMapValue().get(key);

            localExecutionCtx.scope().addVariable(varName, key);
            if (valueVarName != null) {
                localExecutionCtx.scope().addVariable(valueVarName, mapValue);
            }

            Value value = executeStatementBlock(localExecutionCtx, pap, statements);

            if (value instanceof BreakValue) {
                break;
            } else if (value instanceof ReturnValue) {
                return value;
            }

            ctx.scope().local().overwriteFromLocalScope(localExecutionCtx.scope().local());
        }
        return new VoidValue();
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return String.format("%sforeach %s in %s %s",
                indent(indentLevel), (valueVarName != null ? String.format("%s, %s", varName, valueVarName) : varName),
                iter,
                new PMLStatementBlock(statements).toFormattedString(indentLevel)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForeachStatement that = (ForeachStatement) o;
        return Objects.equals(varName, that.varName) && Objects.equals(valueVarName, that.valueVarName) && Objects.equals(iter, that.iter) && Objects.equals(statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varName, valueVarName, iter, statements);
    }

}
