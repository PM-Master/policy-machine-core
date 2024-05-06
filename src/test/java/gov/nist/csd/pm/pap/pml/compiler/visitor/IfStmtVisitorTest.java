package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.BoolLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.IfStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.statement.ShortDeclarationStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IfStmtVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.IfStatementContext ctx = PMLContextVisitor.toCtx(
                """
                if true {
                    x := "a"
                } else if false {
                    x := "b"
                } else {
                    x := "c"
                }
                """,
                PMLParser.IfStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        PMLStatement stmt = new IfStmtVisitor(visitorCtx)
                .visitIfStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new IfStatement(
                        new IfStatement.ConditionalBlock(new BoolLiteral(true), List.of(new ShortDeclarationStatement("x", new StringLiteral("a")))),
                        List.of(new IfStatement.ConditionalBlock(new BoolLiteral(false), List.of(new ShortDeclarationStatement("x", new StringLiteral("b"))))),
                        List.of(new ShortDeclarationStatement("x", new StringLiteral("c")))
                ),
                stmt
        );
    }

    @Test
    void testConditionExpressionsNotBool() throws PMException {
        PMLParser.IfStatementContext ctx = PMLContextVisitor.toCtx(
                """
                if "a" {
                    x := "a"
                } else if "b" {
                    x := "b"
                } else {
                    x := "c"
                }
                """,
                PMLParser.IfStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        PMLStatement stmt = new IfStmtVisitor(visitorCtx)
                .visitIfStatement(ctx);
        assertEquals(2, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type bool, got string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
        assertEquals(
                "expected expression type bool, got string",
                visitorCtx.errorLog().getErrors().get(1).errorMessage()
        );
    }

    @Test
    void testReturnVoidInIf() throws PMException {
        String pml = """
                function f1() {
                    if true {
                        return
                    }
                    
                    create policy class "pc1"
                }
                
                f1()
                """;
        MemoryPolicyModifier store = new MemoryPolicyModifier();
        PMLExecutor.compileAndExecutePML(store, new UserContext(), pml);
        assertFalse(store.graph().nodeExists("pc1"));
    }

}