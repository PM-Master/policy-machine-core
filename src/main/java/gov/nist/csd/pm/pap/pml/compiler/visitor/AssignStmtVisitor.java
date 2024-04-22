package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.statement.AssignStatement;
import gov.nist.csd.pm.pap.pml.type.Type;

public class AssignStmtVisitor extends PMLBaseVisitor<AssignStatement> {

    public AssignStmtVisitor(VisitorContext visitorCtx) {
        super(visitorCtx);
    }

    @Override
    public AssignStatement visitAssignStatement(PMLParser.AssignStatementContext ctx) {
        Expression child = Expression.compile(visitorCtx, ctx.childNode, Type.string());
        Expression parents = Expression.compile(visitorCtx, ctx.parentNodes, Type.array(Type.string()));

        return new AssignStatement(child, parents);
    }
}
