package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.expression.reference.ReferenceByID;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionReturnStatement;
import gov.nist.csd.pm.pap.pml.statement.VariableAssignmentStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.pml.compiler.visitor.CompilerTestUtil.testCompilationError;
import static org.junit.jupiter.api.Assertions.*;

class FunctionInvokeExpressionTest {

    FunctionDefinitionStatement voidFunc = new FunctionDefinitionStatement.Builder("voidFunc")
            .returns(Type.voidType())
            .args(
                    new FormalArgument("a", Type.string()),
                    new FormalArgument("b", Type.string())
            )
            .body(
                    new CreatePolicyStatement(new ReferenceByID("a")),
                    new CreatePolicyStatement(new ReferenceByID("b"))
            )
            .build();

    @Test
    void testVoidReturnType() throws PMException {
        PMLParser.FunctionInvokeExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                voidFunc("a", "b")
                """, PMLParser.FunctionInvokeExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPAP())
                                                                      .withPersistedFunctions(Map.of(voidFunc.getSignature().getFunctionName(), voidFunc.getSignature())));

        Expression e = FunctionInvokeExpression.compileFunctionInvokeExpression(visitorContext, ctx);
        assertEquals(0, visitorContext.errorLog().getErrors().size(), visitorContext.errorLog().getErrors().toString());
        assertEquals(
                new FunctionInvokeExpression("voidFunc", Type.voidType(), List.of(
                        new StringLiteral("a"),
                        new StringLiteral("b")
                )),
                e
        );
        assertEquals(
                Type.voidType(),
                e.getType(visitorContext.scope())
        );

        ExecutionContext executionContext = new ExecutionContext(new UserContext(""), GlobalScope.forExecute(new MemoryPAP())
                                                                                                 .withPersistedFunctions(Map.of(voidFunc.getSignature().getFunctionName(), voidFunc)));
        Value value = e.execute(executionContext, new MemoryPAP());
        assertEquals(
                new VoidValue(),
                value
        );

        assertEquals(
                Type.voidType(),
                value.getType()
        );
    }

    @Test
    void testFunctionNotInScope() throws PMException {
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPAP()));

        testCompilationError(
                """
                voidFunc("a", "b")
                """, visitorCtx, 1,
                "unknown function 'voidFunc' in scope"
        );
    }

    @Test
    void testWrongNumberOfArgs() throws PMException {
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPAP())
                                                                      .withPersistedFunctions(Map.of(voidFunc.getSignature().getFunctionName(), voidFunc.getSignature())));

        testCompilationError(
                """
                voidFunc("a")
                """, visitorCtx, 1,
                "wrong number of args for function call voidFunc: expected 2, got 1"
        );
    }

    @Test
    void testWrongArgType() throws PMException {
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPAP())
                                                                      .withPersistedFunctions(Map.of(voidFunc.getSignature().getFunctionName(), voidFunc.getSignature())));

        testCompilationError(
                """
                voidFunc("a", ["b", "c"])
                """, visitorCtx, 1,
                "invalid argument type: expected string, got []string at arg 1"
        );
    }

    @Test
    void testExecuteReturnValue() throws PMException {
        FunctionDefinitionStatement stringFunc = new FunctionDefinitionStatement.Builder("stringFunc")
                .returns(Type.string())
                .args(
                        new FormalArgument("a", Type.string()),
                        new FormalArgument("b", Type.string())
                )
                .body(
                        new VariableAssignmentStatement("x", false, new StringLiteral("test")),
                        new FunctionReturnStatement(new StringLiteral("test_ret"))
                )
                .build();

        PMLParser.FunctionInvokeExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                stringFunc("a", "b")
                """, PMLParser.FunctionInvokeExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPAP())
                                                                      .withPersistedFunctions(Map.of(stringFunc.getSignature().getFunctionName(), stringFunc.getSignature())));

        Expression e = FunctionInvokeExpression.compileFunctionInvokeExpression(visitorContext, ctx);
        assertEquals(0, visitorContext.errorLog().getErrors().size(), visitorContext.errorLog().getErrors().toString());
        assertEquals(
                Type.string(),
                e.getType(visitorContext.scope())
        );
    }

    @Test
    void testExecuteWithFunctionExecutor() throws PMException {
        FunctionDefinitionStatement stringFunc = new FunctionDefinitionStatement.Builder("stringFunc")
                .returns(Type.string())
                .args(
                        new FormalArgument("a", Type.string()),
                        new FormalArgument("b", Type.string())
                )
                .executor((ctx, policy) -> {
                    return new StringValue("test");
                })
                .build();
        PMLParser.FunctionInvokeExpressionContext ctx = PMLContextVisitor.toExpressionCtx(
                """
                stringFunc("a", "b")
                """, PMLParser.FunctionInvokeExpressionContext.class);
        VisitorContext visitorContext = new VisitorContext(
                GlobalScope.forCompile(new MemoryPAP())
                           .withPersistedFunctions(Map.of(stringFunc.getSignature().getFunctionName(), stringFunc.getSignature()))
        );
        Expression e = FunctionInvokeExpression.compileFunctionInvokeExpression(visitorContext, ctx);
        assertEquals(0, visitorContext.errorLog().getErrors().size(), visitorContext.errorLog().getErrors().toString());

        PAP pap = new MemoryPAP();
        ExecutionContext executionContext =
                new ExecutionContext(
                        new UserContext(""),
                        GlobalScope.forExecute(new MemoryPAP())
                                   .withPersistedFunctions(Map.of(stringFunc.getSignature().getFunctionName(), stringFunc))
                );
        Value value = e.execute(executionContext, pap);
        assertEquals(
                new StringValue("test"),
                value
        );
        assertEquals(
                Type.string(),
                value.getType()
        );
    }

    @Test
    void testChainMethodCall() throws PMException {
        String pml = """
                a("123")
                
                function c(string x) string {
                    return "c" + x
                }
                                
                function b(string x, string y) {
                    create policy class c(x)
                    create policy class c(y)
                }
                                
                function a(string x) {
                    x = "x"
                    y := "y"
                                
                    b(x, y)
                }
                """;
        PAP pap = new MemoryPAP();
        PMLExecutor.compileAndExecutePML(pap, new UserContext(), pml);
        assertTrue(pap.query().graph().nodeExists("cx"));
        assertTrue(pap.query().graph().nodeExists("cy"));
    }

    @Test
    void testReassignArgValueInFunctionDoesNotUpdateVariableOutsideOfScope() throws PMException {
        String pml = """
                x := "test"
                a(x)
                create pc x
                                 
                function a(string x) {
                    x = "x"                               
                }
                """;
        PAP pap = new MemoryPAP();
        PMLExecutor.compileAndExecutePML(pap, new UserContext(), pml);
        assertFalse(pap.query().graph().nodeExists("x"));
        assertTrue(pap.query().graph().nodeExists("test"));
    }

    @Test
    void testReturnInIf() throws PMException {
        String pml = """            
                function a() {
                    if true {
                        return
                    }
                    
                    create pc "pc1"                               
                }
                
                a()
                """;
        PAP pap = new MemoryPAP();
        PMLExecutor.compileAndExecutePML(pap, new UserContext(), pml);
        assertFalse(pap.query().graph().nodeExists("pc1"));
    }
}