package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLCompiler;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationException;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionReturnStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement.*;
import static org.junit.jupiter.api.Assertions.*;

class FunctionDefinitionVisitorTest {

    FunctionSignature testSignature = new FunctionSignature("func1", Type.string(), List.of(
            new FormalArgument("a", Type.string()),
            new FormalArgument("b", Type.bool()),
            new FormalArgument("c", Type.array(Type.string()))
    ));

    @Test
    void testSuccess() throws PMException {
        PMLParser.FunctionDefinitionStatementContext ctx = PMLContextVisitor.toCtx(
                """
                function func1(string a, bool b, []string c) string {
                    return "test"
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPolicyModifier()).withPersistedFunctions(Map.of(testSignature.getFunctionName(), testSignature))
        );
        PMLStatement stmt = new FunctionDefinitionVisitor(visitorCtx)
                .visitFunctionDefinitionStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());

        FunctionDefinitionStatement expected = new Builder("func1")
                .returns(Type.string())
                .args(
                        new FormalArgument("a", Type.string()),
                        new FormalArgument("b", Type.bool()),
                        new FormalArgument("c", Type.array(Type.string()))
                )
                .body(
                        new FunctionReturnStatement(new StringLiteral("test"))
                )
                .build();
        assertEquals(expected, stmt);


        ctx = PMLContextVisitor.toCtx(
                """
                function func1(string a) { 
                    
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);
        visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPolicyModifier()).withPersistedFunctions(Map.of("func1", new FunctionSignature("func1", Type.voidType(), List.of(new FormalArgument("a", Type.string())))))
        );
        stmt = new FunctionDefinitionVisitor(visitorCtx)
                .visitFunctionDefinitionStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size(), visitorCtx.errorLog().getErrors().toString());
        assertEquals(
                new FunctionDefinitionStatement.Builder("func1")
                        .returns(Type.voidType())
                        .args(
                                new FormalArgument("a", Type.string())
                        )
                        .body()
                        .build(),
                stmt
        );
    }

    @Test
    void testNotAllPathsReturn() throws PMException {
        PMLParser.FunctionDefinitionStatementContext ctx = PMLContextVisitor.toCtx(
                """
                function func1(string a, bool b, []string c) string {
                    if true {
                        return "test"
                    } else {
                    
                    }
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPolicyModifier()).withPersistedFunctions(Map.of(testSignature.getFunctionName(), testSignature))
        );
        new FunctionDefinitionVisitor(visitorCtx)
                .visitFunctionDefinitionStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "not all conditional paths return",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                function func1(string a, bool b, []string c) string {
                    foreach x in c {
                        return
                    }
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);
        visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPolicyModifier()).withPersistedFunctions(Map.of(testSignature.getFunctionName(), testSignature))
        );
        new FunctionDefinitionVisitor(visitorCtx)
                .visitFunctionDefinitionStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "not all conditional paths return",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );

        ctx = PMLContextVisitor.toCtx(
                """
                function func1(string a, bool b, []string c) string {
                    
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);
        visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPolicyModifier()).withPersistedFunctions(Map.of(testSignature.getFunctionName(), testSignature))
        );
        new FunctionDefinitionVisitor(visitorCtx)
                .visitFunctionDefinitionStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "not all conditional paths return",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

    @Test
    void testReturnVoidWhenReturnValueIsString() throws PMException {
        PMLParser.FunctionDefinitionStatementContext ctx = PMLContextVisitor.toCtx(
                """
                function func1(string a, bool b, []string c) string {
                    return
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPolicyModifier()).withPersistedFunctions(Map.of(testSignature.getFunctionName(), testSignature))
        );
        new FunctionDefinitionVisitor(visitorCtx)
                .visitFunctionDefinitionStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "return statement \"return\" does not match return type string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

    @Test
    void testWrongTypeOfReturnValue() throws PMException {
        PMLParser.FunctionDefinitionStatementContext ctx = PMLContextVisitor.toCtx(
                """
                function func1(string a, bool b, []string c) string {
                    return false
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPolicyModifier()).withPersistedFunctions(Map.of(testSignature.getFunctionName(), testSignature))
        );
        new FunctionDefinitionVisitor(visitorCtx)
                .visitFunctionDefinitionStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "return statement \"return false\" does not match return type string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

    @Nested
    class FunctionSignatureVisitorTest {

        @Test
        void testDuplicateFormalArgNames() throws PMException {
            String pml = """
                    function func1(string a, bool a) string {
                        return ""
                    }
                    """;
            PMLCompilationException e =
                    assertThrows(PMLCompilationException.class, () -> PMLCompiler.compilePML(new MemoryPolicyModifier(), pml));
            assertEquals(1, e.getErrors().size());
            assertEquals("formal arg 'a' already defined in signature", e.getErrors().get(0).errorMessage());
        }

    }
}