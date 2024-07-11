package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationRuntimeException;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.FunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.scope.UnknownFunctionInScopeException;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.ArrayList;
import java.util.List;

public class FunctionInvokeStmtVisitor extends PMLBaseVisitor<FunctionInvokeExpression> {

    public FunctionInvokeStmtVisitor(VisitorContext visitorCtx) {
        super(visitorCtx);
    }

    @Override
    public FunctionInvokeExpression visitFunctionInvoke(PMLParser.FunctionInvokeContext ctx) {
        return parse(ctx);
    }

    @Override
    public FunctionInvokeExpression visitFunctionInvokeStatement(PMLParser.FunctionInvokeStatementContext ctx) {
        return parse(ctx.functionInvoke());
    }

    private FunctionInvokeExpression parse(PMLParser.FunctionInvokeContext funcCallCtx) {
        String funcName = funcCallCtx.ID().getText();

        // get actual arg expressions
        PMLParser.FunctionInvokeArgsContext funcCallArgsCtx = funcCallCtx.functionInvokeArgs();
        List<Expression> actualArgs = new ArrayList<>();

        PMLParser.ExpressionListContext expressionListContext = funcCallArgsCtx.expressionList();
        if (expressionListContext != null) {
            for (PMLParser.ExpressionContext exprCtx : expressionListContext.expression()) {
                Expression expr = Expression.compile(visitorCtx, exprCtx, Type.any());

                actualArgs.add(expr);
            }
        }

        // check the function is in scope and the args are correct
        FunctionSignature functionSignature;
        try {
            functionSignature = visitorCtx.scope().getFunction(funcName);
        } catch (UnknownFunctionInScopeException e) {
            throw new PMLCompilationRuntimeException(funcCallCtx, e.getMessage());
        }

        // check that the actual args are correct type only if the function is not a pattern function
        // pattern functions are handled differently because we do not want to invoke them now, just
        // prepare them to be invoked during the event processing flow
        List<FormalArg> formalArgs = functionSignature.getArgs();
        if (formalArgs.size() != actualArgs.size()) {
            throw new PMLCompilationRuntimeException(
                    funcCallCtx,
                    "wrong number of args for function call " + funcName + ": " +
                            "expected " + formalArgs.size() + ", got " + actualArgs.size()
            );
        } else if (!(functionSignature instanceof PatternFunctionSignature)){
            for (int i = 0; i < actualArgs.size(); i++) {
                try {
                    Expression actual = actualArgs.get(i);
                    Type actualType = actual.getType(visitorCtx.scope());
                    FormalArg formal = formalArgs.get(i);

                    if (!actual.getType(visitorCtx.scope()).equals(formal.getType())) {
                        throw new PMLCompilationRuntimeException(
                                funcCallCtx,
                                "invalid argument type: expected " + formal.getType() + ", got " +
                                        actualType + " at arg " + i
                        );
                    }
                } catch (PMLScopeException e) {
                    throw new PMLCompilationRuntimeException(funcCallCtx, e.getMessage());
                }
            }
        }

        return new FunctionInvokeExpression(funcName, functionSignature.getReturnType(), actualArgs);
    }
}