package gov.nist.csd.pm.pap.pml.expression;


import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.function.builtin.Equals;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.BoolValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParenExpressionTest {

    private static GlobalScope<Variable, FunctionSignature> compileGlobalScope;
    private static GlobalScope<Value, FunctionDefinitionStatement> executeGlobalScope;

    @BeforeAll
    static void setup() throws PMException {
        compileGlobalScope = GlobalScope.forCompile(new MemoryPAP())
                                        .withPersistedFunctions(Map.of("equals", new Equals().getSignature()));
        executeGlobalScope = GlobalScope.forExecute(new MemoryPAP())
                                        .withPersistedFunctions(Map.of("equals", new Equals()));
    }

    @Test
    void testParenExpression() throws PMException {
        PMLParser.ExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                true && (true || false)
                """, PMLParser.ExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(compileGlobalScope);
        Expression e = Expression.compile(visitorContext, ctx, Type.bool());
        assertEquals(0, visitorContext.errorLog().getErrors().size());
        Value actual = e.execute(new ExecutionContext(new UserContext(""), executeGlobalScope), new MemoryPAP());
        assertEquals(
                new BoolValue(true),
                actual
        );

        ctx = PMLContextVisitor.toExpressionCtx(
                """
                (false || false) && (true || false)
                """, PMLParser.ExpressionContext.class);
        visitorContext = new VisitorContext(compileGlobalScope);
        e = Expression.compile(visitorContext, ctx, Type.bool());
        assertEquals(0, visitorContext.errorLog().getErrors().size());
        actual = e.execute(new ExecutionContext(new UserContext(""), executeGlobalScope), new MemoryPAP());
        assertEquals(
                new BoolValue(false),
                actual
        );

        ctx = PMLContextVisitor.toExpressionCtx(
                """
                (false || false) || (true || false)
                """, PMLParser.ExpressionContext.class);
        visitorContext = new VisitorContext(compileGlobalScope);
        e = Expression.compile(visitorContext, ctx, Type.bool());
        assertEquals(0, visitorContext.errorLog().getErrors().size());
        actual = e.execute(new ExecutionContext(new UserContext(""), executeGlobalScope), new MemoryPAP());
        assertEquals(
                new BoolValue(true),
                actual
        );

        ctx = PMLContextVisitor.toExpressionCtx(
                """
                !(false || false) && (true || false)
                """, PMLParser.ExpressionContext.class);
        visitorContext = new VisitorContext(compileGlobalScope);
        e = Expression.compile(visitorContext, ctx, Type.bool());
        assertEquals(0, visitorContext.errorLog().getErrors().size());
        actual = e.execute(new ExecutionContext(new UserContext(""), executeGlobalScope), new MemoryPAP());
        assertEquals(
                new BoolValue(true),
                actual
        );

        ctx = PMLContextVisitor.toExpressionCtx(
                """
                !(false || false) && (false || false || true)
                """, PMLParser.ExpressionContext.class);
        visitorContext = new VisitorContext(compileGlobalScope);
        e = Expression.compile(visitorContext, ctx, Type.bool());
        assertEquals(0, visitorContext.errorLog().getErrors().size());
        actual = e.execute(new ExecutionContext(new UserContext(""), executeGlobalScope), new MemoryPAP());
        assertEquals(
                new BoolValue(true),
                actual
        );

        ctx = PMLContextVisitor.toExpressionCtx(
                """
                !(false || false) && (false || false || true) && false
                """, PMLParser.ExpressionContext.class);
        visitorContext = new VisitorContext(compileGlobalScope);
        e = Expression.compile(visitorContext, ctx, Type.bool());
        assertEquals(0, visitorContext.errorLog().getErrors().size());
        actual = e.execute(new ExecutionContext(new UserContext(""), executeGlobalScope), new MemoryPAP());
        assertEquals(
                new BoolValue(false),
                actual
        );
    }

    @Test
    void testComplexParen() throws PMException {
        PMLParser.ParenExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                ((true || (true && false)) && (false || (false && true)))
                """,
                PMLParser.ParenExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(compileGlobalScope);
        Expression expression = ParenExpression.compileParenExpression(visitorContext, ctx);
        assertEquals(0, visitorContext.errorLog().getErrors().size());

        PAP pap = new MemoryPAP();
        ExecutionContext executionContext = new ExecutionContext(new UserContext(""), executeGlobalScope);
        Value actual = expression.execute(executionContext, pap);
        assertEquals(
                new BoolValue(false),
                actual
        );
    }

}