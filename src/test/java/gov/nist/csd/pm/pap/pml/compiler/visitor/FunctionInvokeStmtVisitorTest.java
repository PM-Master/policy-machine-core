package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.FunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static gov.nist.csd.pm.pap.pml.compiler.visitor.CompilerTestUtil.testCompilationError;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FunctionInvokeStmtVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.FunctionInvokeStatementContext ctx = PMLContextVisitor.toCtx(
                """
                func1("a", "b", ["c", "d"])
                """,
                PMLParser.FunctionInvokeStatementContext.class);

        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPAP())
                           .withProvidedFunctions(
                                   Map.of(
                                           "func1",
                                           new FunctionSignature(
                                                   "func1",
                                                   Type.string(),
                                                   List.of(
                                                           new FormalArgument("a", Type.string()),
                                                           new FormalArgument("b", Type.string()),
                                                           new FormalArgument("c", Type.array(Type.string()))
                                                   )
                                           )
                                   ))
        );

        PMLStatement stmt = new FunctionInvokeStmtVisitor(visitorCtx)
                .visitFunctionInvokeStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());

        FunctionInvokeExpression expected = new FunctionInvokeExpression(
                "func1",
                Type.string(),
                List.of(
                        new StringLiteral("a"),
                        new StringLiteral("b"),
                        buildArrayLiteral("c", "d")
                )
        );
        assertEquals(expected, stmt);
    }

    @Test
    void testFunctionDoesNotExist() throws PMException {
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPAP()));

        testCompilationError(
                """
                func1("a", "b", ["c", "d"])
                """, visitorCtx, 1,
                "unknown function 'func1' in scope"
        );
    }

    @Test
    void testWrongNumberOfArgs() throws PMException {
        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPAP())
                           .withProvidedFunctions(Map.of(
                                   "func1",
                                   new FunctionSignature(
                                           "func1",
                                           Type.string(),
                                           List.of(
                                                   new FormalArgument("a", Type.string()),
                                                   new FormalArgument("b", Type.string()),
                                                   new FormalArgument("c", Type.array(Type.string()))
                                           )
                                   )
                           ))
        );

        testCompilationError(
                """
                func1("a", "b")
                """, visitorCtx, 1,
                "wrong number of args for function call func1: expected 3, got 2"
        );
    }

    @Test
    void testWrongArgType() throws PMException {
        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPAP())
                           .withProvidedFunctions(Map.of(
                                   "func1",
                                   new FunctionSignature(
                                           "func1",
                                           Type.string(),
                                           List.of(
                                                   new FormalArgument("a", Type.string()),
                                                   new FormalArgument("b", Type.bool())
                                           )
                                   )
                           ))
        );

        testCompilationError(
                """
                func1("a", "b")
                """, visitorCtx, 1,
                "invalid argument type: expected bool, got string at arg 1"
        );
    }

    @Test
    void testNoArgs() throws PMException {
        PMLParser.FunctionInvokeStatementContext ctx = PMLContextVisitor.toCtx(
                """
                func1()
                """,
                PMLParser.FunctionInvokeStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPAP())
                           .withProvidedFunctions(Map.of(
                                   "func1",
                                   new FunctionSignature(
                                           "func1",
                                           Type.string(),
                                           List.of()
                                   )
                           ))
        );
        PMLStatement stmt = new FunctionInvokeStmtVisitor(visitorCtx)
                .visitFunctionInvokeStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());

        FunctionInvokeExpression expected = new FunctionInvokeExpression(
                "func1",
                Type.string(),
                List.of()
        );
        assertEquals(expected, stmt);
    }

}