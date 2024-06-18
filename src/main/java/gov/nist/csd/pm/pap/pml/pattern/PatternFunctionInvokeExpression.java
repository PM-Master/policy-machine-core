package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationRuntimeException;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.FunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.scope.UnknownFunctionInScopeException;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.ArrayList;
import java.util.List;

public class PatternFunctionInvokeExpression extends FunctionInvokeExpression {

    public static PatternFunctionInvokeExpression compile(VisitorContext visitorCtx,
                                                          PMLParser.FunctionInvokeContext functionInvokeContext) {
        String funcName = functionInvokeContext.ID().getText();

        PatternFunctionSignature patternFunctionSignature;
        try {
            FunctionSignature functionSignature = visitorCtx.scope().getFunction(funcName);
            if (functionSignature instanceof PatternFunctionSignature) {
                patternFunctionSignature = (PatternFunctionSignature) functionSignature;
            } else {
                throw new PMLCompilationRuntimeException(functionInvokeContext, "only pattern functions supported");
            }
        } catch (UnknownFunctionInScopeException e) {
            throw new PMLCompilationRuntimeException(functionInvokeContext, e.getMessage());
        }

        List<FormalArgument> formalArgs = patternFunctionSignature.getArgs();
        Type returnType = patternFunctionSignature.getReturnType();

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
            Type formArgType = formalArgs.get(i).getType();

            Expression expr = Expression.compile(visitorCtx, exprCtx, formArgType);
            actualArgs.add(expr);
        }

        return new PatternFunctionInvokeExpression(funcName, returnType, actualArgs);
    }

    public PatternFunctionInvokeExpression(String functionName,
                                           Type result,
                                           List<Expression> actualArgs) {
        super(functionName, result, actualArgs);
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        return new VoidValue();
    }
}
