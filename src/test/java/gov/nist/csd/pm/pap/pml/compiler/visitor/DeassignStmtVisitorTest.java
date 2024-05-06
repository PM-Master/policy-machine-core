package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.DeassignStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DeassignStmtVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.DeassignStatementContext ctx = PMLContextVisitor.toCtx(
                """
                deassign "a" from ["b", "c"]
                """,
                PMLParser.DeassignStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        PMLStatement stmt = new DeassignStmtVisitor(visitorCtx).visitDeassignStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new DeassignStatement(new StringLiteral("a"), buildArrayLiteral("b", "c")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() throws PMException {
        PMLParser.DeassignStatementContext ctx = PMLContextVisitor.toCtx(
                """
                deassign "a" from "c"
                """,
                PMLParser.DeassignStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new DeassignStmtVisitor(visitorCtx).visitDeassignStatement(ctx);
        assertEquals(
                "expected expression type []string, got string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                deassign ["a"] from ["b", "c"]
                """,
                PMLParser.DeassignStatementContext.class);
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new DeassignStmtVisitor(visitorCtx).visitDeassignStatement(ctx);
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

}