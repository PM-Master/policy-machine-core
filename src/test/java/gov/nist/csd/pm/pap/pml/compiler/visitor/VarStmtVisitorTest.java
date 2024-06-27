package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.function.builtin.Equals;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.scope.UnknownVariableInScopeException;
import gov.nist.csd.pm.pap.pml.scope.VariableAlreadyDefinedInScopeException;
import gov.nist.csd.pm.pap.pml.statement.VariableAssignmentStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static gov.nist.csd.pm.pap.pml.compiler.visitor.CompilerTestUtil.testCompilationError;
import static org.junit.jupiter.api.Assertions.*;

class VarStmtVisitorTest {

    private static GlobalScope<Variable, FunctionSignature> testGlobalScope;

    @BeforeAll
    static void setup() throws PMException {
        testGlobalScope = GlobalScope.forCompile(new MemoryPAP())
                                     .withProvidedFunctions(Map.of("equals", new Equals().getSignature()));
    }

    @Nested
    class ConstDeclarationTest {

        @Test
        void testSuccess() throws UnknownVariableInScopeException {
            PMLParser.ConstDeclarationContext ctx = PMLContextVisitor.toCtx(
                    """
                     const x = "a"
                     """, PMLParser.ConstDeclarationContext.class);
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            new VarStmtVisitor(visitorCtx)
                    .visitConstDeclaration(ctx);
            assertEquals(0, visitorCtx.errorLog().getErrors().size());
            assertTrue(visitorCtx.scope().variableExists("x"));
            assertTrue(visitorCtx.scope().getVariable("x").isConst());
        }

        @Test
        void testReassign() throws VariableAlreadyDefinedInScopeException {
            PMLParser.VariableAssignmentStatementContext ctx = PMLContextVisitor.toCtx(
                    """
                     x = "a"
                     """, PMLParser.VariableAssignmentStatementContext.class);
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            visitorCtx.scope().addVariable("x", new Variable("x", Type.string(), true));

            testCompilationError(
                    """
                     x = "a"
                    """, visitorCtx, 1,
                    "cannot reassign const variable"
            );

            PMLParser.ConstDeclarationContext ctx2 = PMLContextVisitor.toCtx(
                    """
                     const x = "a"
                     """, PMLParser.ConstDeclarationContext.class);
            VisitorContext visitorCtx2 = new VisitorContext(testGlobalScope);
            visitorCtx2.scope().addVariable("x", new Variable("x", Type.string(), true));
            testCompilationError(
                    """
                     const x = "a"
                    """, visitorCtx2, 1,
                    "const 'x' already defined in scope"
            );
        }

        @Test
        void testReassignInBlock() {
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);

            testCompilationError(
                    """
                    const (
                        x = "a"
                        x = "b"
                     )
                    """, visitorCtx, 1,
                    "const 'x' already defined in scope"
            );
        }

    }

    @Nested
    class VarDeclarationTest {

        @Test
        void testSuccess() throws UnknownVariableInScopeException {
            PMLParser.VarDeclarationContext ctx = PMLContextVisitor.toCtx(
                    """
                     var x = "a"
                     """, PMLParser.VarDeclarationContext.class);
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            new VarStmtVisitor(visitorCtx)
                    .visitVarDeclaration(ctx);
            assertEquals(0, visitorCtx.errorLog().getErrors().size());
            assertTrue(visitorCtx.scope().variableExists("x"));
            assertFalse(visitorCtx.scope().getVariable("x").isConst());
        }

        @Test
        void testReassign() throws VariableAlreadyDefinedInScopeException {
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            visitorCtx.scope().addVariable("x", new Variable("x", Type.string(), false));
            testCompilationError(
                    """
                     var x = "a"
                    """, visitorCtx, 1,
                    "variable 'x' already defined in scope"
            );
        }

        @Test
        void testReassignConstant() throws VariableAlreadyDefinedInScopeException {
            PMLParser.VarDeclarationContext ctx = PMLContextVisitor.toCtx(
                    """
                     var x = "a"
                     """, PMLParser.VarDeclarationContext.class);
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            visitorCtx.scope().addVariable("x", new Variable("x", Type.string(), true));

            testCompilationError(
                    """
                     var x = "a"
                    """, visitorCtx, 1,
                    "variable 'x' already defined in scope"
            );
        }

        @Test
        void testReassignInBlock() throws VariableAlreadyDefinedInScopeException {
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            visitorCtx.scope().addVariable("x", new Variable("x", Type.string(), true));

            testCompilationError(
                    """
                    var (
                        x = "a"
                        x = "b"
                     )
                    """, visitorCtx, 1,
                    "variable 'x' already defined in scope"
            );
        }

    }

    @Nested
    class ShortDeclarationTest {
        @Test
        void testSuccess() throws UnknownVariableInScopeException {
            PMLParser.ShortDeclarationContext ctx = PMLContextVisitor.toCtx(
                    """
                     x := "a"
                     """, PMLParser.ShortDeclarationContext.class);
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            new VarStmtVisitor(visitorCtx)
                    .visitShortDeclaration(ctx);
            assertEquals(0, visitorCtx.errorLog().getErrors().size());
            assertTrue(visitorCtx.scope().variableExists("x"));
            assertFalse(visitorCtx.scope().getVariable("x").isConst());
        }

        @Test
        void testReassign() throws VariableAlreadyDefinedInScopeException {
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            visitorCtx.scope().addVariable("x", new Variable("x", Type.string(), true));
            testCompilationError(
                    """
                     x := "a"
                    """, visitorCtx, 1,
                    "variable x already exists"
            );
        }
    }

    @Nested
    class VariableAssignmentTest {
        @Test
        void testSuccess() throws UnknownVariableInScopeException, VariableAlreadyDefinedInScopeException {
            PMLParser.VariableAssignmentStatementContext ctx = PMLContextVisitor.toCtx(
                    """
                     x = "a"
                     """, PMLParser.VariableAssignmentStatementContext.class);
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            visitorCtx.scope().addVariable("x", new Variable("x", Type.string(), false));
            VariableAssignmentStatement stmt =
                    (VariableAssignmentStatement) new VarStmtVisitor(visitorCtx)
                            .visitVariableAssignmentStatement(ctx);
            assertEquals(0, visitorCtx.errorLog().getErrors().size());
            assertTrue(visitorCtx.scope().variableExists("x"));
            assertFalse(visitorCtx.scope().getVariable("x").isConst());
            assertFalse(stmt.isPlus());

            ctx = PMLContextVisitor.toCtx(
                    """
                     x += "a"
                     """, PMLParser.VariableAssignmentStatementContext.class);
            visitorCtx = new VisitorContext(testGlobalScope);
            visitorCtx.scope().addVariable("x", new Variable("x", Type.string(), false));
            stmt = (VariableAssignmentStatement) new VarStmtVisitor(visitorCtx)
                    .visitVariableAssignmentStatement(ctx);
            assertEquals(0, visitorCtx.errorLog().getErrors().size());
            assertTrue(visitorCtx.scope().variableExists("x"));
            assertFalse(visitorCtx.scope().getVariable("x").isConst());
            assertTrue(stmt.isPlus());
        }

        @Test
        void testVariableDoesNotExist() {
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);

            testCompilationError(
                    """
                     x = "a"
                    """, visitorCtx, 1,
                    "unknown variable 'x' in scope"
            );
        }

        @Test
        void testVariableIsConstant() throws VariableAlreadyDefinedInScopeException {
            VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
            visitorCtx.scope().addVariable("x", new Variable("x", Type.string(), true));

            testCompilationError(
                    """
                     x = "a"
                    """, visitorCtx, 1,
                    "cannot reassign const variable"
            );
        }
    }
}