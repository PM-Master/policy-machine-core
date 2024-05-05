package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.op.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.expression.FunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.scope.VariableAlreadyDefinedInScopeException;
import gov.nist.csd.pm.pap.pml.statement.CreateRuleStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.ArrayList;
import java.util.List;

public class CreateRuleStmtVisitor extends PMLBaseVisitor<CreateRuleStatement> {

    public CreateRuleStmtVisitor(VisitorContext visitorCtx) {
        super(visitorCtx);
    }

    @Override
    public CreateRuleStatement visitCreateRuleStatement(PMLParser.CreateRuleStatementContext ctx) {
        /*Expression name = Expression.compile(visitorCtx, ctx.ruleName, Type.string());

        FunctionInvokeExpression.compileFunctionInvokeExpression(visitorCtx, ctx.subjectPattern, Type.bool());

        Pattern<Object> subjectPattern = parseFunctionInvoke(ctx.subjectPattern);
        if (subjectPattern == null) {
            return new CreateRuleStatement(ctx);
        }

        Pattern<Object> operationPattern = parseFunctionInvoke(ctx.operationPattern);
        if (subjectPattern == null) {
            return new CreateRuleStatement(ctx);
        }

        List<Pattern<Object>> operandPatterns = new ArrayList<>();
        List<PMLParser.FunctionInvokeContext> operandFuncCtxs = ctx.operandsPattern().functionInvoke();
        for (PMLParser.FunctionInvokeContext operandFuncCtx : operandFuncCtxs) {
            operandPatterns.add(parseFunctionInvoke(operandFuncCtx));
        }


        Expression subjectPatternExpr = Expression.compile(visitorCtx, ctx.subjectPattern, Type.bool());
        Expression operationPatternExpr = Expression.compile(visitorCtx, ctx.operationPattern, Type.bool());
        Expression operandsPatternExpr = Expression.compile(visitorCtx, ctx.operandExpression, Type.array(Type.bool()));
        CreateRuleStatement.ResponseBlock responseBlock;
        try {
            responseBlock = getResponse(ctx.response());
        } catch (VariableAlreadyDefinedInScopeException e) {
            visitorCtx.errorLog().addError(ctx, e.getMessage());

            return new CreateRuleStatement(ctx);
        }

        return new CreateRuleStatement(name, subjectPatternExpr, operationPatternExpr, operandsPatternExpr, responseBlock);*/
        throw new RuntimeException("TODO");
    }

    /*private Pattern<Object> parseFunctionInvoke(PMLParser.FunctionInvokeContext functionInvokeCtx) {
        String functionName = functionInvokeCtx.ID().getText();
        if (functionName.equalsIgnoreCase("subject") ||
                functionName.equalsIgnoreCase("operation")) {

        } else if (functionName.equalsIgnoreCase("operands")){

        } else {
            visitorCtx.errorLog().addError(functionInvokeCtx, "unknown event pattern function " + functionName);

            return null;
        }
    }*/

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

        List<PMLStatement> stmts = new ArrayList<>();
        for (PMLParser.ResponseStatementContext responseStmtCtx : responseStmtsCtx) {
            PMLStatement stmt = null;

            if (responseStmtCtx.statement() != null) {
                stmt = statementVisitor.visitStatement(responseStmtCtx.statement());
            } else if (responseStmtCtx.createRuleStatement() != null) {
                stmt = createRuleStmtVisitor.visitCreateRuleStatement(responseStmtCtx.createRuleStatement());
            } else if (responseStmtCtx.deleteRuleStatement() != null) {
                stmt = deleteRuleStmtVisitor.visitDeleteRuleStatement(responseStmtCtx.deleteRuleStatement());
            }

            if (stmt instanceof FunctionDefinitionStatement) {
                visitorCtx.errorLog().addError(responseStmtCtx, "functions are not allowed inside response blocks");
            }

            stmts.add(stmt);
        }

        return new CreateRuleStatement.ResponseBlock(evtVar, stmts);
    }
}
