package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.statement.SetResourceAccessRightsStatement;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SetResourceAccessRightsStmtVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.SetResourceAccessRightsStatementContext ctx = PMLContextVisitor.toCtx(
                """
                set resource access rights ["a", "b"]
                """,
                PMLParser.SetResourceAccessRightsStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.withVariablesAndSignatures(new MemoryPolicyStore()));
        PMLStatement stmt = new SetResourceAccessRightsStmtVisitor(visitorCtx)
                .visitSetResourceAccessRightsStatement(ctx);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new SetResourceAccessRightsStatement(buildArrayLiteral("a", "b")),
                stmt
        );
    }

    @Test
    void testInvalidExpressions() throws PMException {
        PMLParser.SetResourceAccessRightsStatementContext ctx = PMLContextVisitor.toCtx(
                """
                set resource access rights "a"
                """,
                PMLParser.SetResourceAccessRightsStatementContext.class);
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.withVariablesAndSignatures(new MemoryPolicyStore()));
        new SetResourceAccessRightsStmtVisitor(visitorCtx)
                .visitSetResourceAccessRightsStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "expected expression type []string, got string",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

}