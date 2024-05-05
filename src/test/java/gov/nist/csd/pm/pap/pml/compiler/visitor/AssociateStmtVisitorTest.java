package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.AssociateStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AssociateStmtVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.AssociateStatementContext ctx = PMLContextVisitor.toCtx(
                """
                associate "a" and "b" with ["c", "d"]
                """,
                PMLParser.AssociateStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        PMLStatement stmt = new AssociateStmtVisitor(visitorCtx).visitAssociateStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new AssociateStatement(new StringLiteral("a"), new StringLiteral("b"), buildArrayLiteral("c", "d")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() throws PMException {
        PMLParser.AssociateStatementContext ctx = PMLContextVisitor.toCtx(
                """
                associate ["a"] and "b" with ["c", "d"]
                """,
                PMLParser.AssociateStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
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
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
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
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        new AssociateStmtVisitor(visitorCtx).visitAssociateStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type []string, got string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

}