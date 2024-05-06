package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.NegatedExpression;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.CreateProhibitionStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateProhibitionStmtVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.CreateProhibitionStatementContext ctx = PMLContextVisitor.toCtx(
                """
                create prohibition "test"
                deny user "u1"
                ["read"]
                on union of [!"oa1"]
                """,
                PMLParser.CreateProhibitionStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        PMLStatement stmt = new CreateProhibitionStmtVisitor(visitorCtx).visitCreateProhibitionStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new CreateProhibitionStatement(
                        new StringLiteral("test"),
                        new StringLiteral("u1"),
                        ProhibitionSubject.Type.USER,
                        buildArrayLiteral("read"),
                        false,
                        new ArrayLiteral(Type.string(), new NegatedExpression(new StringLiteral("oa1")))
                ),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() throws PMException {
        PMLParser.CreateProhibitionStatementContext ctx = PMLContextVisitor.toCtx(
                """
                create prohibition ["test"]
                deny user "u1"
                ["read"]
                on union of [!"oa1"]
                """,
                PMLParser.CreateProhibitionStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new CreateProhibitionStmtVisitor(visitorCtx).visitCreateProhibitionStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                create prohibition "test"
                deny user ["u1"]
                ["read"]
                on union of [!"oa1"]
                """,
                PMLParser.CreateProhibitionStatementContext.class);
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new CreateProhibitionStmtVisitor(visitorCtx).visitCreateProhibitionStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                create prohibition "test"
                deny user "u1"
                "read"
                on union of [!"oa1"]
                """,
                PMLParser.CreateProhibitionStatementContext.class);
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new CreateProhibitionStmtVisitor(visitorCtx).visitCreateProhibitionStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type []string, got string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                create prohibition "test"
                deny user "u1"
                ["read"]
                on union of !"oa1"
                """,
                PMLParser.CreateProhibitionStatementContext.class);
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new CreateProhibitionStmtVisitor(visitorCtx).visitCreateProhibitionStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type []string, got string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

    }

}