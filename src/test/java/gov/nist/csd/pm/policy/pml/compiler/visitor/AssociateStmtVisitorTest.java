package gov.nist.csd.pm.policy.pml.compiler.visitor;

import gov.nist.csd.pm.policy.pml.PMLContextVisitor;
import gov.nist.csd.pm.policy.pml.antlr.PMLParser;
import gov.nist.csd.pm.policy.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.policy.pml.model.context.VisitorContext;
import gov.nist.csd.pm.policy.pml.statement.AssignStatement;
import gov.nist.csd.pm.policy.pml.statement.AssociateStatement;
import gov.nist.csd.pm.policy.pml.statement.PMLStatement;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.policy.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.*;

class AssociateStmtVisitorTest {

    @Test
    void testSuccess() {
        PMLParser.AssociateStatementContext ctx = PMLContextVisitor.toCtx(
                """
                associate "a" and "b" with ["c", "d"]
                """,
                PMLParser.AssociateStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext();
        PMLStatement stmt = new AssociateStmtVisitor(visitorCtx).visitAssociateStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new AssociateStatement(new StringLiteral("a"), new StringLiteral("b"), buildArrayLiteral("c", "d")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() {
        PMLParser.AssociateStatementContext ctx = PMLContextVisitor.toCtx(
                """
                associate ["a"] and "b" with ["c", "d"]
                """,
                PMLParser.AssociateStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext();
        new AssociateStmtVisitor(visitorCtx).visitAssociateStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                associate "a" and ["b"] with ["c", "d"]
                """,
                PMLParser.AssociateStatementContext.class);
        visitorCtx = new VisitorContext();
        new AssociateStmtVisitor(visitorCtx).visitAssociateStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                associate "a" and "b" with "c"
                """,
                PMLParser.AssociateStatementContext.class);
        visitorCtx = new VisitorContext();
        new AssociateStmtVisitor(visitorCtx).visitAssociateStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type []string, got string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

}