package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.exception.PMLExecutionException;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.function.FunctionExecutor;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.scope.UnknownFunctionInScopeException;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ReturnValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionInvokeExpression extends Expression {

    public FunctionInvokeExpression(PMLParser.FunctionInvokeContext funcCallCtx) {
        super(funcCallCtx);
    }

    public static Expression compileFunctionInvokeExpression(VisitorContext visitorCtx,
                                                             PMLParser.FunctionInvokeExpressionContext functionInvokeExpressionContext) {
        PMLParser.FunctionInvokeContext functionInvokeContext = functionInvokeExpressionContext.functionInvoke();
        String funcName = functionInvokeContext.ID().getText();

        FunctionSignature functionSignature;
        try {
            functionSignature = visitorCtx.scope().getFunction(funcName);
        } catch (UnknownFunctionInScopeException e) {
            visitorCtx.errorLog().addError(functionInvokeContext, e.getMessage());

            return new ErrorExpression(functionInvokeExpressionContext);
        }

        List<FormalArgument> formalArgs = functionSignature.getArgs();
        PMLParser.FunctionInvokeArgsContext funcCallArgsCtx = functionInvokeContext.functionInvokeArgs();
        List<PMLParser.ExpressionContext> argExpressions =  new ArrayList<>();
        PMLParser.ExpressionListContext expressionListContext = funcCallArgsCtx.expressionList();
        if (expressionListContext != null) {
            argExpressions = expressionListContext.expression();
        }

        if (formalArgs.size() != argExpressions.size()) {
            visitorCtx.errorLog().addError(
                    functionInvokeContext,
                    "wrong number of args for function call " + funcName + ": " +
                            "expected " + formalArgs.size() + ", got " + argExpressions.size()
            );

            return new ErrorExpression(functionInvokeExpressionContext);
        }

        List<Expression> actualArgs = new ArrayList<>();
        for (int i = 0; i < argExpressions.size(); i++) {
            PMLParser.ExpressionContext exprCtx = argExpressions.get(i);
            FormalArgument formalArgument = formalArgs.get(i);

            Expression expr = Expression.compile(visitorCtx, exprCtx, formalArgument.type());
            if (expr instanceof ErrorExpression) {
                return expr;
            }

            actualArgs.add(expr);
        }

        // get result types
        Type returnType = functionSignature.getReturnType();

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
    public Type getType(Scope<Variable, FunctionSignature> scope) throws PMLScopeException {
        return result;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyModification policyModification) throws PMException {
        FunctionDefinitionStatement funcDef = ctx.scope().getFunction(functionName);

        ExecutionContext invokeCtx = prepareFunctionInvoke(ctx, policyModification, funcDef);

        Value value = new VoidValue();
        if (funcDef.isFunctionExecutor()) {
            FunctionExecutor functionExecutor = funcDef.getFunctionExecutor();
            try {
                value = functionExecutor.exec(invokeCtx, policyModification);
            } catch (PMLScopeException e) {
                throw new PMLExecutionException(e);
            }
        } else {
            List<PMLStatement> statements = funcDef.getStatements();
            for (PMLStatement stmt : statements) {
                value = stmt.execute(invokeCtx, policyModification);
                if (value instanceof ReturnValue) {
                    break;
                }
            }
        }

        ctx.scope().local().overwriteFromLocalScope(ctx.scope().local());

        return value;
    }

    private ExecutionContext prepareFunctionInvoke(ExecutionContext ctx, PolicyModification policyModification, FunctionDefinitionStatement funcDef)
            throws PMException {
        String funcName = funcDef.getSignature().getFunctionName();
        List<FormalArgument> formalArgs = funcDef.getSignature().getArgs();

        if (formalArgs.size() != actualArgs.size()) {
            throw new PMLExecutionException("expected " + formalArgs.size() + " args for function \""
                                                    + funcName + "\", got " + actualArgs.size());
        }

        ExecutionContext funcInvokeExecCtx = ctx.copy();

        for (int i = 0; i < actualArgs.size(); i++) {
            Expression argExpr = actualArgs.get(i);
            Value argValue = argExpr.execute(funcInvokeExecCtx, policyModification);
            FormalArgument formalArg = formalArgs.get(i);

            if (!argValue.getType().equals(formalArg.type())) {
                throw new PMLExecutionException("expected " + formalArg.type() + " for arg " + i + " for function \""
                                                        + funcName + "\", got " + argValue.getType());
            }

            funcInvokeExecCtx.scope().local().addOrOverwriteVariable(formalArg.name(), argValue);
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
}
