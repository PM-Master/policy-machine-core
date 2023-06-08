package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.CompiledPML;
import gov.nist.csd.pm.pap.pml.PMLCompiler;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationException;
import gov.nist.csd.pm.pap.pml.pattern.PatternExpression;
import gov.nist.csd.pm.pap.pml.pattern.*;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.statement.CreateObligationStatement;
import gov.nist.csd.pm.pap.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.pap.pml.statement.CreateRuleStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CreateRuleStmtVisitorTest {

    private PatternFunctionInvokeExpression pAnyExpr = new PatternFunctionInvokeExpression("pAny", Type.any(), List.of());
    private PatternFunctionInvokeExpression pEqualsU1Expr = new PatternFunctionInvokeExpression("pEquals", Type.any(), List.of(
            new StringLiteral("u1")
    ));
    private PatternFunctionInvokeExpression pEqualsExprTestEvent = new PatternFunctionInvokeExpression("pEquals", Type.any(), List.of(
            new StringLiteral("test_event")
    ));
    private PatternFunctionInvokeExpression pContainedInExpression = new PatternFunctionInvokeExpression("pContainedIn", Type.any(), List.of(
            new ArrayLiteral(Type.string(), new StringLiteral("u1"), new StringLiteral("u2"))
    ));
    private FunctionSignature pAnySig = new AnyPatternFunction().getSignature();
    private FunctionSignature pEqualsSig = new EqualsPatternFunction().getSignature();
    private FunctionSignature pContainedInSig = new ContainedInPatternFunction().getSignature();

    @Test
    void testSubjectClause() throws PMException {
        String pml = """
                    create obligation "obligation1" {
                        create rule "any user"
                        when subject => pAny()
                        performs op => pEquals("test_event")
                        do(ctx) {}
                        
                        create rule "users"
                        when subject => pEquals("u1")
                        performs op => pEquals("test_event")
                        do(ctx) {}
                        
                        create rule "users list"
                        when subject => pContainedIn(["u1", "u2"])
                        performs op => pEquals("test_event")
                        do(ctx) {}
                    }
                    """;
        CompiledPML compiledPML = PMLCompiler.compilePML(new MemoryPAP(), pml);
        assertEquals(1, compiledPML.stmts().size());

        CreateObligationStatement stmt = (CreateObligationStatement)compiledPML.stmts().get(0);
        assertEquals(
                new CreateObligationStatement(
                        new StringLiteral("obligation1"),
                        List.of(
                                new CreateRuleStatement(
                                        new StringLiteral("any user"),
                                        new PatternExpression("subject", pAnyExpr),
                                        new PatternExpression("op", pEqualsExprTestEvent),
                                        new ArrayList<>(),
                                        new CreateRuleStatement.ResponseBlock("ctx", new ArrayList<>())

                                ),
                                new CreateRuleStatement(
                                        new StringLiteral("users"),
                                        new PatternExpression("subject", pEqualsU1Expr),
                                        new PatternExpression("op", pEqualsExprTestEvent),
                                        new ArrayList<>(),
                                        new CreateRuleStatement.ResponseBlock("ctx", new ArrayList<>())

                                ),
                                new CreateRuleStatement(
                                        new StringLiteral("users list"),
                                        new PatternExpression("subject", pContainedInExpression),
                                        new PatternExpression("op", pEqualsExprTestEvent),
                                        new ArrayList<>(),
                                        new CreateRuleStatement.ResponseBlock("ctx", new ArrayList<>())

                                )
                        )
                ),
                stmt
        );
    }

    @Test
    void testPerformsClause() throws PMException {
        String pml = """
                    create obligation "obligation1" {
                        create rule "r1"
                        when subject => pAny()
                        performs op => pAny()
                        do(ctx) {}
                    }
                    """;
        CompiledPML compiledPML = PMLCompiler.compilePML(new MemoryPAP(), pml);
        assertEquals(1, compiledPML.stmts().size());

        CreateObligationStatement stmt = (CreateObligationStatement)compiledPML.stmts().get(0);
        CreateObligationStatement expected = new CreateObligationStatement(
                new StringLiteral("obligation1"),
                List.of(
                        new CreateRuleStatement(
                                new StringLiteral("r1"),
                                new PatternExpression("subject", pAnyExpr),
                                new PatternExpression("op", pAnyExpr),
                                new ArrayList<>(),
                                new CreateRuleStatement.ResponseBlock("ctx", new ArrayList<>())
                        )
                )
        );
        assertEquals(expected, stmt);

        String pml2 = """
            create obligation "obligation1" {
                create rule "r1"
                when subject => pAny()
                do(ctx) {}
            }
            """;
        PMLCompilationException e = assertThrows(
                PMLCompilationException.class,
                () -> PMLCompiler.compilePML(new MemoryPAP(), pml2)
        );
        assertEquals(1, e.getErrors().size());
        assertEquals("mismatched input 'do' expecting 'performs'", e.getErrors().getFirst().errorMessage());
    }

    @Test
    void testOnClause() throws PMException {
        String pml = """
                    create obligation "obligation1" {
                        create rule "any operand"
                        when subject => pAny()
                        performs op => pAny()
                        do(ctx) {}
                        
                        create rule "any operand with on"
                        when subject => pAny()
                        performs op => pAny()
                        on
                        do(ctx) {}
                        
                        create rule "an operand"
                        when subject => pAny()
                        performs op => pAny()
                        on op1 => pAny()
                        do(ctx) {}
                    }
                    """;
        CompiledPML compiledPML = PMLCompiler.compilePML(new MemoryPAP(), pml);
        assertEquals(1, compiledPML.stmts().size());

        CreateObligationStatement stmt = (CreateObligationStatement)compiledPML.stmts().get(0);
        CreateObligationStatement expected = new CreateObligationStatement(
                new StringLiteral("obligation1"),
                List.of(
                        new CreateRuleStatement(
                                new StringLiteral("any operand"),
                                new PatternExpression("subject", pAnyExpr),
                                new PatternExpression("op", pAnyExpr),
                                new ArrayList<>(),
                                new CreateRuleStatement.ResponseBlock("ctx", new ArrayList<>())
                        ),
                        new CreateRuleStatement(
                                new StringLiteral("any operand with on"),
                                new PatternExpression("subject", pAnyExpr),
                                new PatternExpression("op", pAnyExpr),
                                new ArrayList<>(),
                                new CreateRuleStatement.ResponseBlock("ctx", new ArrayList<>())
                        ),
                        new CreateRuleStatement(
                                new StringLiteral("an operand"),
                                new PatternExpression("subject", pAnyExpr),
                                new PatternExpression("op", pAnyExpr),
                                List.of(
                                        new PatternExpression("op1", pAnyExpr)
                                ),
                                new CreateRuleStatement.ResponseBlock("ctx", new ArrayList<>())
                        )
                )
        );

        assertEquals(expected, stmt);
    }

    @Test
    void testResponse() throws PMException {
        String pml = """
                    create obligation "obligation1" {
                        create rule "r1"
                        when subject => pAny()
                        performs op => pAny()
                        do(ctx) {
                            create policy class "pc1"
                            create policy class "pc2"
                        }
                    }
                    """;
        CompiledPML compiledPML = PMLCompiler.compilePML(new MemoryPAP(), pml);
        assertEquals(1, compiledPML.stmts().size());

        CreateObligationStatement stmt = (CreateObligationStatement)compiledPML.stmts().getFirst();
        CreateObligationStatement expected = new CreateObligationStatement(
                new StringLiteral("obligation1"),
                List.of(
                        new CreateRuleStatement(
                                new StringLiteral("r1"),
                                new PatternExpression("subject", pAnyExpr),
                                new PatternExpression("op", pAnyExpr),
                                new ArrayList<>(),
                                new CreateRuleStatement.ResponseBlock("ctx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("pc1")),
                                        new CreatePolicyStatement(new StringLiteral("pc2"))
                                ))
                        )
                )
        );
        assertEquals(expected, stmt);
    }

    @Test
    void testFunctionInResponseReturnsError() throws PMException {
        String pml = """
                    create obligation "obligation1" {
                        create rule "e1 and e2"
                        when subject => pAny()
                        performs op => pAny()
                        do(ctx) {
                            function f1() {}
                        }
                    }
                    """;
        PMLCompilationException e = assertThrows(
                PMLCompilationException.class,
                () -> PMLCompiler.compilePML(new MemoryPAP(), pml)
        );
        assertEquals(
                "functions are not allowed inside response blocks",
                e.getErrors().get(0).errorMessage()
        );
    }

    @Test
    void testReturnValueInResponseThrowsException() {
        String pml = """
                    create obligation "obligation1" {
                        create rule "any user"
                        when subject => pAny()
                        performs op => pEquals(op, "test_event")
                        do(ctx) {
                            return "test"
                        }
                    }
                    """;
        assertThrows(PMLCompilationException.class, () -> PMLCompiler.compilePML(new MemoryPAP(), pml));
    }
}
