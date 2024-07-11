package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationRuntimeException;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.FunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.expression.LogicalExpression;
import gov.nist.csd.pm.pap.pml.expression.literal.Literal;
import gov.nist.csd.pm.pap.pml.pattern.PMLPatternFunction;
import gov.nist.csd.pm.pap.pml.pattern.PatternExpression;
import gov.nist.csd.pm.pap.pml.pattern.PatternFunctionExpression;
import gov.nist.csd.pm.pap.pml.scope.UnknownFunctionInScopeException;
import gov.nist.csd.pm.pap.pml.scope.VariableAlreadyDefinedInScopeException;
import gov.nist.csd.pm.pap.pml.statement.*;
import gov.nist.csd.pm.pap.pml.statement.operation.CreateOperationStatement;
import gov.nist.csd.pm.pap.pml.statement.operation.CreateRuleStatement;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.ArrayList;
import java.util.List;

public class CreateRuleStmtVisitor extends PMLBaseVisitor<CreateRuleStatement> {

    public CreateRuleStmtVisitor(VisitorContext visitorCtx) {
        super(visitorCtx);
    }

    @Override
    public CreateRuleStatement visitCreateRuleStatement(PMLParser.CreateRuleStatementContext ctx) {
        Expression name = Expression.compile(visitorCtx, ctx.ruleName, Type.string());

        try {
            PatternExpression subjectExpr = parsePattern(ctx.subjectPattern, Type.string());
            if (subjectExpr == null) {
                throw new PMLCompilationRuntimeException(ctx, "subject cannot be null");
            }

            PatternExpression operationPattern = parsePattern(ctx.operationPattern, Type.string());
            if (operationPattern == null) {
                throw new PMLCompilationRuntimeException(ctx, "operation cannot be null");
            }

            List<PatternExpression> operandPatterns = new ArrayList<>();
            if (ctx.operandPatterns != null) {
                for (PMLParser.PatternContext patternContext : ctx.operandPatterns.pattern()) {
                    PatternExpression operandExpr = parsePattern(patternContext, Type.any());
                    operandPatterns.add(operandExpr);
                }
            }

            CreateRuleStatement.ResponseBlock responseBlock = getResponse(ctx.response());

            return new CreateRuleStatement(name, subjectExpr, operationPattern, operandPatterns, responseBlock);
        } catch (UnknownFunctionInScopeException | VariableAlreadyDefinedInScopeException e) {
            throw new PMLCompilationRuntimeException(e);
        }
    }


    private Expression parsePattern(PMLParser.PatternContext ctx, Type varType)
            throws VariableAlreadyDefinedInScopeException, UnknownFunctionInScopeException {
        if (ctx == null) {
            return null;
        }

        VisitorContext copy = visitorCtx.copy();
        String varName = ctx.ID().getText();
        copy.scope().addVariable(varName, new Variable(varName, varType, true));

        return parseFunctionInvoke(copy, varName, ctx);
    }

    private PatternExpression parseFunctionInvoke(VisitorContext copy, String varName, PMLParser.PatternContext ctx)
            throws UnknownFunctionInScopeException {
        Expression expression = Expression.compile(copy, ctx.expression(), Type.bool());
        checkPatternExpressions(ctx, expression);

        PMLFunction<?> function = copy.scope().getFunction(expression.getFunctionName());
        if (!(function instanceof PMLPatternFunction)) {
            throw new PMLCompilationRuntimeException(ctx, "only pattern functions are supported here");
        }

        return new PatternExpression(varName, expression);
    }

    private static void checkPatternExpressions(PMLParser.PatternContext ctx, Expression expression) {
        if (expression instanceof LogicalExpression logicalExpression) {
            checkPatternExpressions(ctx, logicalExpression.getLeft());
            checkPatternExpressions(ctx, logicalExpression.getRight());
        } else if (expression instanceof FunctionInvokeExpression functionInvokeExpression) {
            List<Expression> actualArgs = functionInvokeExpression.getActualArgs();
            for (Expression actualArg : actualArgs) {
                checkPatternExpressions(ctx, actualArg);
            }
        } else if (expression instanceof Literal) {
            return;
        }

        throw new PMLCompilationRuntimeException(ctx, "pattern expected pattern function invoke or logical expression");
    }

    private CreateRuleStatement.ResponseBlock getResponse(PMLParser.ResponseContext ctx) throws VariableAlreadyDefinedInScopeException {
        String evtVar = ctx.ID().getText();

        // create a new local parser scope for the response block
        // add the event name and event context map to the local parser scope
        VisitorContext localVisitorCtx = visitorCtx.copy();
        localVisitorCtx.scope().addVariable(evtVar, new Variable(evtVar, Type.map(Type.string(), Type.any()), true));

        PMLParser.ResponseBlockContext responseBlockCtx = ctx.responseBlock();
        List<PMLParser.ResponseStatementContext> responseStmtsCtx = responseBlockCtx.responseStatement();

        StatementVisitor statementVisitor = new StatementVisitor(localVisitorCtx);
        CreateRuleStmtVisitor createRuleStmtVisitor = new CreateRuleStmtVisitor(localVisitorCtx);
        DeleteRuleStmtVisitor deleteRuleStmtVisitor = new DeleteRuleStmtVisitor(localVisitorCtx);

        List<PMLStatementSerializer> stmts = new ArrayList<>();
        for (PMLParser.ResponseStatementContext responseStmtCtx : responseStmtsCtx) {
            PMLStatementSerializer stmt = null;

            if (responseStmtCtx.statement() != null) {
                stmt = statementVisitor.visitStatement(responseStmtCtx.statement());
            } else if (responseStmtCtx.createRuleStatement() != null) {
                create rule might need to be a PMLStatement but it doesnt need to be a OperationStatement

                stmt = createRuleStmtVisitor.visitCreateRuleStatement(responseStmtCtx.createRuleStatement());
            } else if (responseStmtCtx.deleteRuleStatement() != null) {
                stmt = deleteRuleStmtVisitor.visitDeleteRuleStatement(responseStmtCtx.deleteRuleStatement());
            }

            if (stmt instanceof CreateOperationStatement) {
                throw new PMLCompilationRuntimeException(responseStmtCtx, "functions are not allowed inside response blocks");
            }

            stmts.add(stmt);
        }

        return new CreateRuleStatement.ResponseBlock(evtVar, stmts);
    }
}
