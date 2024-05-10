package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.PatternValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PatternExpression extends Expression {

    private String varName;
    private PatternFunctionInvokeExpression invokeExpr;

    public PatternExpression(String varName, PatternFunctionInvokeExpression invokeExpr) {
        this.varName = varName;
        this.invokeExpr = invokeExpr;
    }

    @Override
    public Type getType(Scope<Variable, FunctionSignature> scope) throws PMLScopeException {
        return Type.bool();
    }

    @Override
    public PatternValue execute(ExecutionContext ctx, PAP pap) throws PMException {
        FunctionDefinitionStatement function = ctx.scope().getFunction(invokeExpr.getFunctionName());
        if (!(function instanceof PMLPatternFunctionStmt pmlFunc)) {
            throw new PMException("Function '" + invokeExpr.getFunctionName() + "' is not a PMLPatternFunctionStmt");
        }

        List<Expression> actualArgs = invokeExpr.getActualArgs();
        List<Value> args = new ArrayList<>();
        for (Expression expr : actualArgs) {
            if (expr instanceof PatternFunctionInvokeExpression funcExpr){
                args.add(new PatternExpression(varName, funcExpr).execute(ctx, pap));
            } else {
                args.add(expr.execute(ctx, pap));
            }
        }

        PMLPattern pattern = pmlFunc.getPattern(varName, args);
        return new PatternValue(pattern);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PatternExpression that = (PatternExpression) o;
        return Objects.equals(varName, that.varName) && Objects.equals(invokeExpr, that.invokeExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varName, invokeExpr);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return String.format("(%s) => %s", varName, invokeExpr);
    }
}
