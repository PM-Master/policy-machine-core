package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.BoolLiteral;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.BoolValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogicalExpressionTest {

    @Test
    void testCompile() throws PMException {
        PMLParser.LogicalExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                true && false
                """,
                PMLParser.LogicalExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        Expression expression = LogicalExpression.compileLogicalExpression(visitorContext, ctx);
        assertEquals(0, visitorContext.errorLog().getErrors().size());

        LogicalExpression logicalExpression = (LogicalExpression) expression;
        assertEquals(
                new LogicalExpression(new BoolLiteral(true), new BoolLiteral(false), true),
                logicalExpression
        );
    }

    @Test
    void testExecute() throws PMException {
        PMLParser.LogicalExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                true && false
                """,
                PMLParser.LogicalExpressionContext.class);
        MemoryPolicyModifier store = new MemoryPolicyModifier();

        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(store));
        Expression expression = LogicalExpression.compileLogicalExpression(visitorContext, ctx);
        assertEquals(0, visitorContext.errorLog().getErrors().size());

        ExecutionContext executionContext = new ExecutionContext(new UserContext(""), GlobalScope.forExecute(store));
        Value actual = expression.execute(executionContext, store);
        assertEquals(
                new BoolValue(false),
                actual
        );

        ctx = PMLContextVisitor.toExpressionCtx(
                """
                false || true
                """,
                PMLParser.LogicalExpressionContext.class);
        visitorContext = new VisitorContext(GlobalScope.forCompile(store));
        expression = LogicalExpression.compileLogicalExpression(visitorContext, ctx);
        assertEquals(0, visitorContext.errorLog().getErrors().size());

        store = new MemoryPolicyModifier();
        executionContext = new ExecutionContext(new UserContext(""), GlobalScope.forExecute(store));
        actual = expression.execute(executionContext, store);
        assertEquals(
                new BoolValue(true),
                actual
        );
    }
}