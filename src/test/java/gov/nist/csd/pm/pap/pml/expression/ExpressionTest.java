package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.expression.reference.ReferenceByID;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExpressionTest {

    @Test
    void testAllowedTypes() throws PMException {
        PMLParser.VariableReferenceExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                a
                """, PMLParser.VariableReferenceExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        visitorContext.scope().addVariable("a", new Variable("a", Type.string(), false));
        Expression actual = Expression.compile(visitorContext, ctx, Type.string());
        assertEquals(
                new ReferenceByID("a"),
                actual
        );

        ctx = PMLContextVisitor.toExpressionCtx(
                """
                a
                """, PMLParser.VariableReferenceExpressionContext.class);
        visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        visitorContext.scope().addVariable("a", new Variable("a", Type.array(Type.string()), false));
        actual = Expression.compile(visitorContext, ctx, Type.array(Type.string()));
        assertEquals(
                new ReferenceByID("a"),
                actual
        );
    }

    @Test
    void testDisallowedTypes() throws PMException {
        PMLParser.VariableReferenceExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                a
                """, PMLParser.VariableReferenceExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        visitorContext.scope().addVariable("a", new Variable("a", Type.string(), false));
        Expression e = Expression.compile(visitorContext, ctx, Type.array(Type.string()));
        assertTrue(e instanceof ErrorExpression);
        assertEquals(1, visitorContext.errorLog().getErrors().size());
        assertEquals(
                "expected expression type []string, got string",
                visitorContext.errorLog().getErrors().get(0).errorMessage()
        );

    }


    @Test
    void testCompileStringExpression_Literal() throws PMException {
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        Expression expression = Expression.fromString(visitorContext, "\"test\"", Type.string());
        assertEquals(0, visitorContext.errorLog().getErrors().size());
        assertEquals(new StringLiteral("test"), expression);
    }

    @Test
    void testCompileStringExpression_VarRef() throws PMException {
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        visitorContext.scope().addVariable("test", new Variable("test", Type.string(), true));
        Expression expression = Expression.fromString(visitorContext, "test", Type.string());
        assertEquals(0, visitorContext.errorLog().getErrors().size());
        assertEquals(new ReferenceByID("test"), expression);
    }

    @Test
    void testCompileStringExpression_FuncInvoke() throws PMException {
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore())
                                                                   .withPersistedFunctions(Map.of("test", new FunctionSignature("test", Type.string(), List.of()))));

        Expression expression = Expression.fromString(visitorContext, "test()", Type.string());
        assertEquals(0, visitorContext.errorLog().getErrors().size());
        assertEquals(new FunctionInvokeExpression("test", Type.string(), List.of()), expression);
    }

    @Test
    void testCompileStringExpression_NonString_Error() throws PMException {
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        Expression.fromString(visitorContext, "\"test\" == \"test\"", Type.string());
        assertEquals(1, visitorContext.errorLog().getErrors().size());

        visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        Expression.fromString(visitorContext, "[\"a\", \"b\"]", Type.string());
        assertEquals(1, visitorContext.errorLog().getErrors().size());
    }

}