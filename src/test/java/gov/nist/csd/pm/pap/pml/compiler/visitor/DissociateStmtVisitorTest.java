package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.DissociateStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DissociateStmtVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.DissociateStatementContext ctx = PMLContextVisitor.toCtx(
                """
                dissociate "a" and ["b"]
                """,
                PMLParser.DissociateStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        PMLStatement stmt = new DissociateStmtVisitor(visitorCtx).visitDissociateStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new DissociateStatement(new StringLiteral("a"), buildArrayLiteral("b")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() throws PMException {
        PMLParser.DissociateStatementContext ctx = PMLContextVisitor.toCtx(
                """
                dissociate ["a"] and "b"
                """,
                PMLParser.DissociateStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new DissociateStmtVisitor(visitorCtx).visitDissociateStatement(ctx);
        assertEquals(2, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
        assertEquals(
                "expected expression type []string, got string",
                visitorCtx.errorLog().getErrors().get(1).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                dissociate "a" and "b"
                """,
                PMLParser.DissociateStatementContext.class);
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new DissociateStmtVisitor(visitorCtx).visitDissociateStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type []string, got string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

}