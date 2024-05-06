package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.DeleteRuleStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeleteRuleStmtVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.DeleteRuleStatementContext ctx = PMLContextVisitor.toCtx(
                """
                delete rule "rule1" from obligation "obl1"
                """,
                PMLParser.DeleteRuleStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        PMLStatement stmt = new DeleteRuleStmtVisitor(visitorCtx).visitDeleteRuleStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new DeleteRuleStatement(new StringLiteral("rule1"), new StringLiteral("obl1")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() throws PMException {
        PMLParser.DeleteRuleStatementContext ctx = PMLContextVisitor.toCtx(
                """
                delete rule ["rule1"] from obligation "obl1"
                """,
                PMLParser.DeleteRuleStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new DeleteRuleStmtVisitor(visitorCtx).visitDeleteRuleStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                delete rule "rule1" from obligation ["obl1"]
                """,
                PMLParser.DeleteRuleStatementContext.class);
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new DeleteRuleStmtVisitor(visitorCtx).visitDeleteRuleStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

}