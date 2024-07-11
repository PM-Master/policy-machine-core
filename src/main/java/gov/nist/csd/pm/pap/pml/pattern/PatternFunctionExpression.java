package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationRuntimeException;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.FunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.expression.LogicalExpression;
import gov.nist.csd.pm.pap.pml.function.PMLFunction;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.ArrayList;
import java.util.List;

public class PatternFunctionExpression extends Expression {

    public static PatternFunctionExpression compile(VisitorContext visitorCtx,
                                                    PMLParser.PatternContext ctx) {
        Expression e = Expression.compile(visitorCtx, ctx.expression(), Type.bool());
        checkPatternExpressions(ctx, e);

        String funcName = ctx.ID().getText();

        PMLFunction<?> function = visitorCtx.scope().global().getFunction(funcName);

        List<RequiredCapability> formalArgs = function.getCapMap();
        Type returnType = function.getReturnType();


        PMLParser.FunctionInvokeArgsContext funcCallArgsCtx = .functionInvokeArgs();
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
            Type formArgType = formalArgs.get(i).getType();

            Expression expr = Expression.compile(visitorCtx, exprCtx, formArgType);
            actualArgs.add(expr);
        }

        return new PatternFunctionExpression(funcName, returnType, actualArgs);
    }

    private static void checkPatternExpressions(PMLParser.PatternContext ctx, Expression expression) {
        if (expression instanceof LogicalExpression logicalExpression) {
            checkPatternExpressions(ctx, logicalExpression.getLeft());
            checkPatternExpressions(ctx, logicalExpression.getRight());
        } else if (expression instanceof FunctionInvokeExpression functionInvokeExpression &&
                functionInvokeExpression instanceof PatternFunctionExpression) {
                return;
        }

        throw new PMLCompilationRuntimeException(ctx, "pattern expected pattern function invoke or logical expression");
    }

    public PatternFunctionExpression(String functionName,
                                     Type result,
                                     List<Expression> actualArgs) {
        super(functionName, result, actualArgs);
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        return new VoidValue();
    }
}
