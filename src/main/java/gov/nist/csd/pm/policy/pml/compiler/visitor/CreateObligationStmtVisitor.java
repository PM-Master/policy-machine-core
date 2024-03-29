package gov.nist.csd.pm.policy.pml.compiler.visitor;

import gov.nist.csd.pm.policy.pml.antlr.PMLBaseVisitor;
import gov.nist.csd.pm.policy.pml.antlr.PMLParser;
import gov.nist.csd.pm.policy.pml.model.context.VisitorContext;
import gov.nist.csd.pm.policy.pml.model.expression.Type;
import gov.nist.csd.pm.policy.pml.statement.CreateObligationStatement;
import gov.nist.csd.pm.policy.pml.statement.Expression;
import gov.nist.csd.pm.policy.pml.statement.PMLStatement;

import java.util.ArrayList;
import java.util.List;

public class CreateObligationStmtVisitor extends PMLBaseVisitor<CreateObligationStatement> {

    private final VisitorContext visitorCtx;

    public CreateObligationStmtVisitor(VisitorContext visitorCtx) {
        this.visitorCtx = visitorCtx;
    }

    @Override
    public CreateObligationStatement visitCreateObligationStatement(PMLParser.CreateObligationStatementContext ctx) {
        Expression name = Expression.compile(visitorCtx, ctx.expression(), Type.string());

        List<PMLStatement> ruleStmts = new ArrayList<>();
        for (PMLParser.CreateRuleStatementContext ruleStmt : ctx.createRuleStatement()) {
            PMLStatement createRuleStmt = new CreateRuleStmtVisitor(visitorCtx)
                    .visitCreateRuleStatement(ruleStmt);
            ruleStmts.add(createRuleStmt);
        }

        return new CreateObligationStatement(name, ruleStmts);
    }
}
