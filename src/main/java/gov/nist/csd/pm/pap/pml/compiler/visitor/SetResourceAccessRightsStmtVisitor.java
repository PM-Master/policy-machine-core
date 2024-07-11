package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.statement.operation.SetResourceOperationsStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.antlr.v4.runtime.tree.TerminalNode;

public class SetResourceAccessRightsStmtVisitor extends PMLBaseVisitor<SetResourceOperationsStatement> {

    public SetResourceAccessRightsStmtVisitor(VisitorContext visitorCtx) {
        super(visitorCtx);
    }

    @Override
    public SetResourceOperationsStatement visitSetResourceAccessRightsStatement(PMLParser.SetResourceAccessRightsStatementContext ctx) {
        ArrayLiteral arrayLiteral = new ArrayLiteral(Type.string());
        for (TerminalNode terminalNode : ctx.accessRightsArr.ID()) {
            arrayLiteral.add(new StringLiteral(terminalNode.getText()));
        }


        return new SetResourceOperationsStatement(arrayLiteral);
    }
}
