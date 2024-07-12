package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationRuntimeException;
import gov.nist.csd.pm.pap.pml.exception.PMLExecutionException;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.function.PMLFunction;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.pattern2.PMLPatternFunction;
import gov.nist.csd.pm.pap.pml.pattern2.PatternFunctionInvokeExpression;
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

        Map<String, PMLRequiredCapability> capMap = function.getPMLCapMap();
        Type returnType = function.getReturnType();

        PMLParser.FunctionInvokeArgsContext funcCallArgsCtx = functionInvokeContext.functionInvokeArgs();
        List<PMLParser.ExpressionContext> argExpressions =  new ArrayList<>();
        PMLParser.ExpressionListContext expressionListContext = funcCallArgsCtx.expressionList();
        if (expressionListContext != null) {
            argExpressions = expressionListContext.expression();
        }

        if (capMap.size() != argExpressions.size()) {
            throw new PMLCompilationRuntimeException(
                    functionInvokeContext,
                    "wrong number of args for function call " + funcName + ": " +
                            "expected " + capMap.size() + ", got " + argExpressions.size()
            );
        }

        List<Expression> actualArgs = new ArrayList<>();
        for (int i = 0; i < argExpressions.size(); i++) {
            PMLParser.ExpressionContext exprCtx = argExpressions.get(i);
            Map.Entry<String, PMLRequiredCapability> entry = getReqCapAtIndex(i, capMap);

            Expression expr = Expression.compile(visitorCtx, exprCtx, entry.getValue().type());
            actualArgs.add(expr);
        }

        if (function instanceof PMLPatternFunction) {
            return new PatternFunctionInvokeExpression(function, actualArgs);
        } else {
            return new FunctionInvokeExpression(function, actualArgs);
        }
    }

    private PMLFunction function;
    private List<Expression> actualArgs;

    public FunctionInvokeExpression(PMLFunction function, List<Expression> actualArgs) {
        this.function = function;
        this.actualArgs = actualArgs;
    }

    public PMLFunction getFunction() {
        return function;
    }

    public List<Expression> getActualArgs() {
        return actualArgs;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        Map<String, PMLRequiredCapability> capMap = function.getPMLCapMap();
        ExecutionContext funcInvokeExecCtx = ctx.copy();
        Map<String, Object> operands = new HashMap<>();

        for (int i = 0; i < actualArgs.size(); i++) {
            Expression argExpr = actualArgs.get(i);
            Value argValue = funcInvokeExecCtx.executeStatement(pap, argExpr);
            Map.Entry<String, PMLRequiredCapability> cap = getReqCapAtIndex(i, capMap);

            if (cap == null) {
                throw new PMLExecutionException("arg index " + i + " out of bounds for function " + function.getName());
            } else if (!argValue.getType().equals(cap.getValue().type())) {
                throw new PMLExecutionException("expected " + cap.getValue().type() + " for arg " + i + " for function \""
                                                        + function.getName() + "\", got " + argValue.getType());
            }

            operands.put(cap.getKey(), argValue);
        }

        Value value = function.withOperands(operands)
                             .withCtx(ctx)
                             .execute(pap);

        ctx.scope().local().overwriteFromLocalScope(funcInvokeExecCtx.scope().local());

        return value;
    }

    protected static Map.Entry<String, PMLRequiredCapability> getReqCapAtIndex(int index, Map<String, PMLRequiredCapability> capMap) {
        for (Map.Entry<String, PMLRequiredCapability> entry : capMap.entrySet()) {
            if (index == entry.getValue().order()) {
                return entry;
            }
        }

        return null;
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return String.format("%s(%s)", function.getName(), argsToString());
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
        if (!(o instanceof FunctionInvokeExpression that)) {
            return false;
        }
        return Objects.equals(function, that.function) && Objects.equals(actualArgs, that.actualArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, actualArgs);
    }

    @Override
    public Type getType(Scope<Variable> scope) throws PMLScopeException {
        return function.getReturnType();
    }
}
