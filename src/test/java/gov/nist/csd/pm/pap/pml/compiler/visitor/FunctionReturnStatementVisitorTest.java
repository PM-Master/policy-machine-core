package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLContextVisitor;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FunctionReturnStatementVisitorTest {

    @Test
    void testSuccess() throws PMException {
        fail("TODO");
        /*PMLParser.FunctionDefinitionStatementContext ctx1 = PMLContextVisitor.toCtx(
                """
                function func1(string a, bool b, []string c) string {
                    return "test"
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);

        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPolicyStore())
                           .withPersistedFunctions(Map.of(
                                   "func1",
                                   new FunctionSignature(
                                           "func1",
                                           Type.string(),
                                           List.of(
                                                   new FormalArgument("a", Type.string()),
                                                   new FormalArgument("b", Type.bool()),
                                                   new FormalArgument("c", Type.array(Type.string()))))
                           ))
        );

        FunctionDefinitionStatement functionDefinitionStatement = (FunctionDefinitionStatement) new FunctionDefinitionVisitor(visitorCtx)
                .visitFunctionDefinitionStatement(ctx1);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(1, functionDefinitionStatement.getBody().size());
        assertEquals(
                new FunctionReturnStatement(new StringLiteral("test")),
                functionDefinitionStatement.getBody().get(0)
        );

        PMLParser.CreateObligationStatementContext ctx2 = PMLContextVisitor.toCtx(
                """
                        create obligation "test" {
                            create rule "test"
                            when users ["u1"]
                            performs ["e1"]
                            do(ctx) {
                                return
                            }
                        }
                        """,
                PMLParser.CreateObligationStatementContext.class
        );
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyStore()));
        CreateObligationStatement createObligationStatement = new CreateObligationStmtVisitor(visitorCtx)
                .visitCreateObligationStatement(ctx2);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new FunctionReturnStatement(),
                createObligationStatement.getRuleStmts().get(0).getResponse().getStatements().get(0)
        );*/
    }

    @Test
    void testReturnStatementNotInFunctionOrResponse() throws PMException {
        PMLParser.ReturnStatementContext ctx = PMLContextVisitor.toCtx(
                """
                        return
                        """,
                PMLParser.ReturnStatementContext.class
        );
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new FunctionReturnStmtVisitor(visitorCtx)
                .visitReturnStatement(ctx);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "return statement not in function definition or obligation response",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

    @Test
    void testReturnStatementWithValueInResponse() throws PMException {
        PMLParser.CreateObligationStatementContext ctx2 = PMLContextVisitor.toCtx(
                """
                        create obligation "test" {
                            create rule "test"
                            when users ["u1"]
                            performs ["e1"]
                            do(ctx) {
                                return "test"
                            }
                        }
                        """,
                PMLParser.CreateObligationStatementContext.class
        );
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPolicyModifier()));
        new CreateObligationStmtVisitor(visitorCtx)
                .visitCreateObligationStatement(ctx2);
        assertEquals(1, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                "return statement in response cannot return a value",
                visitorCtx.errorLog().getErrors().get(0).errorMessage()
        );
    }

}