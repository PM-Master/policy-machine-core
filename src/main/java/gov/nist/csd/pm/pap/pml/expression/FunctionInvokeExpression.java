package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationRuntimeException;
import gov.nist.csd.pm.pap.pml.exception.PMLExecutionException;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.scope.UnknownFunctionInScopeException;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.*;

public class FunctionInvokeExpression extends Expression {

    public static Expression compileFunctionInvokeExpression(VisitorContext visitorCtx,
                                                             PMLParser.FunctionInvokeExpressionContext functionInvokeExpressionContext) {
        PMLParser.FunctionInvokeContext functionInvokeContext = functionInvokeExpressionContext.functionInvoke();
        return compileFunctionInvoke(visitorCtx, functionInvokeContext);
    }

    public static Expression compileFunctionInvoke(VisitorContext visitorCtx, PMLParser.FunctionInvokeContext functionInvokeContext) {
        String funcName = functionInvokeContext.ID().getText();

        PMLFunction function;
        try {
            function = visitorCtx.scope().getFunction(funcName);
        } catch (UnknownFunctionInScopeException e) {
            throw new PMLCompilationRuntimeException(functionInvokeContext, e.getMessage());
        }

        List<PMLRequiredCapability> formalArgs = function.getPmlCapMap();
        Type returnType = function.getReturnType();

        PMLParser.FunctionInvokeArgsContext funcCallArgsCtx = functionInvokeContext.functionInvokeArgs();
        List<PMLParser.ExpressionContext> argExpressions =  new ArrayList<>();
        PMLParser.ExpressionListContext expressionListContext = funcCallArgsCtx.expressionList();
        if (expressionListContext != null) {
            argExpressions = expressionListContext.expression();
        }

        if (formalArgs.size() != argExpressions.size()) {
            throw new PMLCompilationRuntimeException(
                    functionInvokeContext,
                    "wrong number of args for function call " + funcName + ": " +
                            "expected " + formalArgs.size() + ", got " + argExpressions.size()
            );
        }

        List<Expression> actualArgs = new ArrayList<>();
        for (int i = 0; i < argExpressions.size(); i++) {
            PMLParser.ExpressionContext exprCtx = argExpressions.get(i);
            Type formArgType = formalArgs.get(i).type();

            Expression expr = Expression.compile(visitorCtx, exprCtx, formArgType);
            actualArgs.add(expr);
        }


        return new FunctionInvokeExpression(funcName, returnType, actualArgs);
    }

    private String functionName;
    private Type result;
    private List<Expression> actualArgs;

    public FunctionInvokeExpression(String functionName, Type result, List<Expression> actualArgs) {
        this.functionName = functionName;
        this.result = result;
        this.actualArgs = actualArgs;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Type getResult() {
        return result;
    }

    public List<Expression> getActualArgs() {
        return actualArgs;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        PMLFunction funcDef = ctx.scope().getFunction(functionName);

        ExecutionContext invokeCtx = prepareFunctionInvoke(ctx, pap, funcDef);

        List<Value> values = new ArrayList<>();
        for (Expression e : actualArgs) {
            Value value = e.execute(ctx, pap);

            values.add(value);
        }

        Value value = funcDef.withCtx(invokeCtx)
                             .withOperands(values)
                             .execute(pap);

        ctx.scope().local().overwriteFromLocalScope(invokeCtx.scope().local());

        return value;
    }

    private ExecutionContext prepareFunctionInvoke(ExecutionContext ctx, PAP pap, PMLFunction funcDef)
            throws PMException {
        String funcName = funcDef.getName();
        List<PMLRequiredCapability> formalArgs = funcDef.getPmlCapMap();

        if (formalArgs.size() != actualArgs.size()) {
            throw new PMLExecutionException("expected " + formalArgs.size() + " args for function \""
                    + funcName + "\", got " + actualArgs.size());
        }

        ExecutionContext funcInvokeExecCtx = ctx.copy();

        for (int i = 0; i < actualArgs.size(); i++) {
            Expression argExpr = actualArgs.get(i);
            Value argValue = funcInvokeExecCtx.executeStatement(pap, argExpr);
            PMLRequiredCapability formalArg = formalArgs.get(i);

            if (!argValue.getType().equals(formalArg.type())) {
                throw new PMLExecutionException("expected " + formalArg.type() + " for arg " + i + " for function \""
                        + funcName + "\", got " + argValue.getType());
            }
        }

        return funcInvokeExecCtx;
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return String.format("%s(%s)", functionName, argsToString());
    }

    private String argsToString() {
        StringBuilder s = new StringBuilder();
        for (Expression arg : actualArgs) {
            if (s.length() > 0) {
                s.append(", ");
            }
            s.append(arg);
        }

        return s.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FunctionInvokeExpression that = (FunctionInvokeExpression) o;
        return Objects.equals(functionName, that.functionName) && Objects.equals(
                result, that.result) && Objects.equals(actualArgs, that.actualArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName, result, actualArgs);
    }

    @Override
    public Type getType(Scope<Variable> scope) throws PMLScopeException {
        return result;
    }
}
