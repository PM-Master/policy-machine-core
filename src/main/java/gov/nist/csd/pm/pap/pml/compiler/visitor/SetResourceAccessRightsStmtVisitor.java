package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.statement.SetResourceAccessRightsStatement;
import gov.nist.csd.pm.pap.pml.type.Type;

public class SetResourceAccessRightsStmtVisitor extends PMLBaseVisitor<SetResourceAccessRightsStatement> {

    public SetResourceAccessRightsStmtVisitor(VisitorContext visitorCtx) {
        super(visitorCtx);
    }

    @Override
    public SetResourceAccessRightsStatement visitSetResourceAccessRightsStatement(PMLParser.SetResourceAccessRightsStatementContext ctx) {
        Expression exprList = Expression.compile(visitorCtx, ctx.accessRights, Type.array(Type.string()));

        return new SetResourceAccessRightsStatement(exprList);
    }
}
