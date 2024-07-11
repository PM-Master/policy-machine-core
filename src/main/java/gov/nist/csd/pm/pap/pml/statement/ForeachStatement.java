package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.*;

import java.util.List;
import java.util.Objects;

public class ForeachStatement implements PMLStatement {

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
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        if (statements.isEmpty()) {
            return new VoidValue();
        }

        Value iterValue = ctx.executeStatement(pap, iter);
        if (iterValue instanceof ArrayValue arrayValue) {
            return executeArrayIterator(ctx, arrayValue, pap);
        } else if (iterValue instanceof MapValue mapValue) {
            return executeMapIterator(ctx, mapValue, pap);
        }

        return new VoidValue();
    }

    private Value executeArrayIterator(ExecutionContext ctx, ArrayValue iterValue, PAP pap) throws PMException{
        for (Value v : iterValue.getValue()) {
            ExecutionContext localExecutionCtx = ctx.copy();

            localExecutionCtx.scope().addVariable(varName, v);

            Value value = localExecutionCtx.executeStatements(pap, statements);

            if (value instanceof BreakValue) {
                break;
            } else if (value instanceof ReturnValue) {
                return value;
            }

            ctx.scope().local().overwriteFromLocalScope(localExecutionCtx.scope().local());
        }
        return new VoidValue();
    }

    private Value executeMapIterator(ExecutionContext ctx, MapValue iterValue, PAP pap) throws PMException{
        for (Value key : iterValue.getValue().keySet()) {
            ExecutionContext localExecutionCtx = ctx.copy();

            Value mapValue = iterValue.getMapValue().get(key);

            localExecutionCtx.scope().addVariable(varName, key);
            if (valueVarName != null) {
                localExecutionCtx.scope().addVariable(valueVarName, mapValue);
            }

            Value value = localExecutionCtx.executeStatements(pap, statements);

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
        if (this == o) {
            return true;
        }
        if (!(o instanceof ForeachStatement that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(varName, that.varName) && Objects.equals(
                valueVarName,
                that.valueVarName
        ) && Objects.equals(iter, that.iter) && Objects.equals(statements, that.statements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), varName, valueVarName, iter, statements);
    }
}
