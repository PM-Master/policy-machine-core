package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.function.builtin.Equals;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.CreateNonPCStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static gov.nist.csd.pm.pap.pml.PMLUtil.buildMapLiteral;
import static gov.nist.csd.pm.pap.pml.compiler.visitor.CompilerTestUtil.testCompilationError;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateNonPCStmtVisitorTest {

    private static GlobalScope<Variable, FunctionSignature> testGlobalScope;

    @BeforeAll
    static void setup() throws PMException {
        testGlobalScope = GlobalScope.forCompile(new MemoryPAP())
                .withProvidedFunctions(Map.of("equals", new Equals().getSignature()));
    }

    @Test
    void testSuccess() {
        PMLParser.CreateNonPCStatementContext ctx = PMLContextVisitor.toCtx(
                """
                create user attribute "ua1" with properties {"k": "v"} assign to ["a"]
                """,
                PMLParser.CreateNonPCStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
        PMLStatement stmt = new CreateNonPCStmtVisitor(visitorCtx).visitCreateNonPCStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new CreateNonPCStatement(new StringLiteral("ua1"), NodeType.UA, buildArrayLiteral("a"), buildMapLiteral("k", "v")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() {
        VisitorContext visitorCtx = new VisitorContext(testGlobalScope);
        testCompilationError(
                """
                create user attribute ["ua1"] with properties {"k": "v"} assign to ["a"]
                """, visitorCtx, 1,
                "expected expression type string, got []string"
        );

        testCompilationError(
                """
                create user attribute "ua1" with properties ["k", "v"] assign to ["a"]
                """, visitorCtx, 1,
                "expected expression type map[string]string, got []string"
        );

        testCompilationError(
                """
                create user attribute "ua1" with properties {"k": "v"} assign to "a"
                """, visitorCtx, 1,
                "expected expression type []string, got string"
        );
    }

}