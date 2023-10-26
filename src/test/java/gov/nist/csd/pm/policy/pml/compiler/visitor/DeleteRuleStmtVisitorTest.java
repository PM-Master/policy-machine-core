package gov.nist.csd.pm.policy.pml.compiler.visitor;

import gov.nist.csd.pm.policy.pml.PMLContextVisitor;
import gov.nist.csd.pm.policy.pml.antlr.PMLParser;
import gov.nist.csd.pm.policy.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.policy.pml.model.context.VisitorContext;
import gov.nist.csd.pm.policy.pml.statement.AssignStatement;
import gov.nist.csd.pm.policy.pml.statement.DeleteRuleStatement;
import gov.nist.csd.pm.policy.pml.statement.PMLStatement;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.policy.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.*;

class DeleteRuleStmtVisitorTest {

    @Test
    void testSuccess(){
        PMLParser.DeleteRuleStatementContext ctx = PMLContextVisitor.toCtx(
                """
                delete rule "rule1" from obligation "obl1"
                """,
                PMLParser.DeleteRuleStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext();
        PMLStatement stmt = new DeleteRuleStmtVisitor(visitorCtx).visitDeleteRuleStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new DeleteRuleStatement(new StringLiteral("rule1"), new StringLiteral("obl1")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() {
        PMLParser.DeleteRuleStatementContext ctx = PMLContextVisitor.toCtx(
                """
                delete rule ["rule1"] from obligation "obl1"
                """,
                PMLParser.DeleteRuleStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext();
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
        visitorCtx = new VisitorContext();
        new DeleteRuleStmtVisitor(visitorCtx).visitDeleteRuleStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

}