package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionReturnStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.pml.compiler.visitor.CompilerTestUtil.testCompilationError;
import static gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement.*;
import static org.junit.jupiter.api.Assertions.*;

class FunctionDefinitionVisitorTest {

    FunctionSignature testSignature = new FunctionSignature("func1", Type.string(), List.of(
            new FormalArgument("a", Type.string()),
            new FormalArgument("b", Type.bool()),
            new FormalArgument("c", Type.array(Type.string()))
    ));

    VisitorContext visitorCtx = new VisitorContext(
            GlobalScope.forCompile(new MemoryPAP()).withProvidedFunctions(Map.of(testSignature.getFunctionName(), testSignature))
    );

    FunctionDefinitionVisitorTest() throws PMException {
    }


    @Test
    void testSuccess() throws PMException {
        PMLParser.FunctionDefinitionStatementContext ctx = PMLContextVisitor.toCtx(
                """
                function func1(string a, bool b, []string c) string {
                    return "test"
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);
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
                GlobalScope.forCompile(new MemoryPAP()).withProvidedFunctions(Map.of("func1", new FunctionSignature("func1", Type.voidType(), List.of(new FormalArgument("a", Type.string())))))
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
    void testNotAllPathsReturn() {
        testCompilationError(
                """
                function func1(string a, bool b, []string c) string {
                    if true {
                        return "test"
                    } else {
                    
                    }
                }
                """, visitorCtx, 1,
                "not all conditional paths return"
        );

        testCompilationError(
                """
                function func1(string a, bool b, []string c) string {
                    foreach x in c {
                        return
                    }
                }
                """, visitorCtx, 1,
                "not all conditional paths return"
        );

        testCompilationError(
                """
                function func1(string a, bool b, []string c) string {
                    
                }
                """, visitorCtx, 1,
                "not all conditional paths return"
        );
    }

    @Test
    void testReturnVoidWhenReturnValueIsString() throws PMException {
        testCompilationError(
                """
                function func1(string a, bool b, []string c) string {
                    return
                }
                """, visitorCtx, 1,
                "return statement \"return\" does not match return type string"
        );
    }

    @Test
    void testWrongTypeOfReturnValue() throws PMException {
        testCompilationError(
                """
                function func1(string a, bool b, []string c) string {
                    return false
                }
                """, visitorCtx, 1,
                "return statement \"return false\" does not match return type string"
        );
    }

    @Nested
    class FunctionSignatureVisitorTest {
        @Test
        void testDuplicateFormalArgNames() throws PMException {
            testCompilationError(
                    """
                    function func1(string a, bool a) string {
                        return ""
                    }
                    """, visitorCtx, 1,
                    "formal arg 'a' already defined in signature"
            );
        }

    }
}