package gov.nist.csd.pm.policy.pml.compiler.visitor;

import gov.nist.csd.pm.policy.pml.PMLContextVisitor;
import gov.nist.csd.pm.policy.pml.antlr.PMLParser;
import gov.nist.csd.pm.policy.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.policy.pml.model.context.VisitorContext;
import gov.nist.csd.pm.policy.pml.statement.AssignStatement;
import gov.nist.csd.pm.policy.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.policy.pml.statement.PMLStatement;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.policy.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.*;

class AssignStmtVisitorTest {

    @Test
    void testSuccess() {
        PMLParser.AssignStatementContext ctx = PMLContextVisitor.toCtx(
                """
                assign "a" to ["b", "c"]
                """,
                PMLParser.AssignStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext();
        PMLStatement stmt = new AssignStmtVisitor(visitorCtx).visitAssignStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new AssignStatement(new StringLiteral("a"), buildArrayLiteral("b", "c")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() {
        PMLParser.AssignStatementContext ctx = PMLContextVisitor.toCtx(
                """
                assign "a" to "b"
                """,
                PMLParser.AssignStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext();
        new AssignStmtVisitor(visitorCtx).visitAssignStatement(ctx);
        assertEquals(
                "expected expression type []string, got string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                assign ["a"] to "b"
                """,
                PMLParser.AssignStatementContext.class);
        visitorCtx = new VisitorContext();
        new AssignStmtVisitor(visitorCtx).visitAssignStatement(ctx);
        assertEquals(2, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

}