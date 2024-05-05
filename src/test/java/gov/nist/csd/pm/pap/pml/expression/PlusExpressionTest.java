package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlusExpressionTest {

    @Test
    void testPlus() throws PMException {
        PMLParser.PlusExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                "a" + "b"
                """,
                PMLParser.PlusExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        Expression expression = PlusExpression.compilePlusExpression(visitorContext, ctx);
        assertEquals(0, visitorContext.errorLog().getErrors().size());

        PlusExpression plusExpression = (PlusExpression) expression;
        assertEquals(
                new PlusExpression(new StringLiteral("a"), new StringLiteral("b")),
                plusExpression
        );

        Value value = plusExpression.execute(new ExecutionContext(new UserContext(""), GlobalScope.forExecute(new MemoryPolicyStore())), new MemoryPolicyStore());
        assertEquals(
                new StringValue("ab"),
                value
        );
    }

    @Test
    void testPlus3Expressions() throws PMException {
        PMLParser.PlusExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                "a" + "b" + "c"
                """,
                PMLParser.PlusExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        Expression expression = PlusExpression.compilePlusExpression(visitorContext, ctx);
        assertEquals(0, visitorContext.errorLog().getErrors().size());

        PlusExpression plusExpression = (PlusExpression) expression;
        assertEquals(
                new PlusExpression(new PlusExpression(new StringLiteral("a"), new StringLiteral("b")), new StringLiteral("c")),
                plusExpression
        );

        Value value = plusExpression.execute(new ExecutionContext(new UserContext(""), GlobalScope.forExecute(new MemoryPolicyStore())), new MemoryPolicyStore());
        assertEquals(
                new StringValue("abc"),
                value
        );
    }

    @Test
    void testNonStringType() throws PMException {
        PMLParser.PlusExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                "a" + "b" + ["c"]
                """,
                PMLParser.PlusExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        PlusExpression.compilePlusExpression(visitorContext, ctx);
        assertEquals(1, visitorContext.errorLog().getErrors().size());
        assertEquals(
                "expected expression type string, got []string",
                visitorContext.errorLog().getErrors().get(0).errorMessage()
        );
    }

}