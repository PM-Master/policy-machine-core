package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.DissociateStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static gov.nist.csd.pm.pap.pml.compiler.visitor.CompilerTestUtil.testCompilationError;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DissociateStmtVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.DissociateStatementContext ctx = PMLContextVisitor.toCtx(
                """
                dissociate "a" and ["b"]
                """,
                PMLParser.DissociateStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPAP()));
        PMLStatement stmt = new DissociateStmtVisitor(visitorCtx).visitDissociateStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new DissociateStatement(new StringLiteral("a"), buildArrayLiteral("b")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() throws PMException {
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPAP()));

        testCompilationError(
                """
                dissociate ["a"] and "b"
                """, visitorCtx, 1,
                "expected expression type string, got []string"
                );

        testCompilationError(
                """
                dissociate "a" and "b"
                """, visitorCtx, 1,
                "expected expression type []string, got string"
        );
    }

}