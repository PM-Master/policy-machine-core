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
import gov.nist.csd.pm.pap.pml.statement.CreateObligationStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionReturnStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.pml.compiler.visitor.CompilerTestUtil.testCompilationError;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class FunctionReturnStatementVisitorTest {

    @Test
    void testSuccess() throws PMException {
        PMLParser.FunctionDefinitionStatementContext ctx1 = PMLContextVisitor.toCtx(
                """
                function func1(string a, bool b, []string c) string {
                    return "test"
                }
                """,
                PMLParser.FunctionDefinitionStatementContext.class);

        VisitorContext visitorCtx = new VisitorContext(
                GlobalScope.forCompile(new MemoryPAP())
                           .withProvidedFunctions(Map.of(
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

        FunctionDefinitionStatement functionDefinitionStatement = new FunctionDefinitionVisitor(visitorCtx)
                .visitFunctionDefinitionStatement(ctx1);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(1, functionDefinitionStatement.getStatements().size());
        assertEquals(
                new FunctionReturnStatement(new StringLiteral("test")),
                functionDefinitionStatement.getStatements().get(0)
        );

        PMLParser.CreateObligationStatementContext ctx2 = PMLContextVisitor.toCtx(
                """
                        create obligation "test" {
                            create rule "test"
                            when subject => pEquals("u1")
                            performs op => pEquals("e1")
                            do(ctx) {
                                return
                            }
                        }
                        """,
                PMLParser.CreateObligationStatementContext.class
        );
        visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPAP()));
        CreateObligationStatement createObligationStatement = new CreateObligationStmtVisitor(visitorCtx)
                .visitCreateObligationStatement(ctx2);
        assertEquals(0, visitorCtx.errorLog().getErrors().size());
        assertEquals(
                new FunctionReturnStatement(),
                createObligationStatement.getRuleStmts().get(0).getResponse().getStatements().get(0)
        );
    }

    @Test
    void testReturnStatementNotInFunctionOrResponse() throws PMException {
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPAP()));

        testCompilationError(
                """
                        return
                """, visitorCtx, 1,
                "return statement not in function definition or obligation response"
        );
    }

    @Test
    void testReturnStatementWithValueInResponse() throws PMException {
        VisitorContext visitorCtx = new VisitorContext(GlobalScope.forCompile(new MemoryPAP()));

        testCompilationError(
                """
                create obligation "test" {
                    create rule "test"
                    when subject => pAny()
                    performs op => pAny()
                    do(ctx) {
                        return "test"
                    }
                }
                """, visitorCtx, 1,
                "return statement in response cannot return a value"
        );
    }

}