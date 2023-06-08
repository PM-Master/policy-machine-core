package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicy;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.pattern.PatternExpression;
import gov.nist.csd.pm.pap.pml.pattern.PatternFunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.query.UserContext;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static gov.nist.csd.pm.pap.pml.pattern.AnyPatternFunction.pAny;
import static gov.nist.csd.pm.pap.pml.pattern.ContainedInPatternFunction.pContainedIn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateObligationStatementTest {

    @Test
    void testSuccess() throws PMException, PMException {
        CreateObligationStatement stmt = new CreateObligationStatement(new StringLiteral("o1"), List.of(
                new CreateRuleStatement(
                        new StringLiteral("rule1"),
                        new PatternExpression("subject", new PatternFunctionInvokeExpression("pAny", Type.any(), List.of())),
                        new PatternExpression("op", new PatternFunctionInvokeExpression("pContainedIn", Type.any(), List.of(
                                buildArrayLiteral("e1", "e2")
                        ))),
                        List.of(
                                new PatternExpression("opnd1", new PatternFunctionInvokeExpression("pContainedIn", Type.any(), List.of(
                                        buildArrayLiteral("oa1", "oa2")
                                )))
                        ),
                        new CreateRuleStatement.ResponseBlock("evtCtx", List.of(
                                new CreatePolicyStatement(new StringLiteral("pc2"))
                        ))
                )
        ));

        MemoryPAP pap = new MemoryPAP();
        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createUser("u2", new HashMap<>(), List.of("ua2"));
        pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
        ExecutionContext execCtx = new ExecutionContext(new UserContext("u2"), GlobalScope.forExecute(pap));

        stmt.execute(execCtx, pap);

        assertTrue(pap.query().obligations().exists("o1"));

        Obligation actual = pap.query().obligations().get("o1");
        assertEquals(1, actual.getRules().size());
        assertEquals("u2", actual.getAuthor().getUser());
        Rule rule = actual.getRules().get(0);
        assertEquals("rule1", rule.getName());
        assertEquals(new EventPattern(
                pAny("subject"),
                pContainedIn("op", List.of("e1", "e2")),
                List.of(
                        pContainedIn("opnd1", List.of("oa1", "oa2"))
                )
        ), rule.getEventPattern());
    }

    @Test
    void testToFormattedString() {
        CreateObligationStatement stmt = new CreateObligationStatement(
                new StringLiteral("obl1"),
                List.of(
                        new CreateRuleStatement(
                                new StringLiteral("rule1"),
                                new PatternExpression("subject", new PatternFunctionInvokeExpression("pAny", Type.any(), List.of())),
                                new PatternExpression("op", new PatternFunctionInvokeExpression("pContainedIn", Type.any(), List.of(
                                        buildArrayLiteral("e1", "e2")
                                ))),
                                List.of(
                                        new PatternExpression("opnd1", new PatternFunctionInvokeExpression("pContainedIn", Type.any(), List.of(
                                                buildArrayLiteral("oa1", "oa2")
                                        )))
                                ),
                                new CreateRuleStatement.ResponseBlock("evtCtx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("pc2"))
                                ))
                        ),
                        new CreateRuleStatement(
                                new StringLiteral("rule2"),
                                new PatternExpression("subject", new PatternFunctionInvokeExpression("pEquals", Type.any(), List.of(
                                        new StringLiteral("u1")
                                ))),
                                new PatternExpression("op", new PatternFunctionInvokeExpression("pEquals", Type.any(), List.of(
                                        new StringLiteral("e3")
                                ))),
                                List.of(
                                        new PatternExpression("opnd1", new PatternFunctionInvokeExpression("pContainedIn", Type.any(), List.of(
                                                buildArrayLiteral("oa1", "oa2")
                                        )))
                                ),
                                new CreateRuleStatement.ResponseBlock("evtCtx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("pc3"))
                                ))
                        )
                )

        );
        assertEquals(
                """
                        create obligation "obl1" {
                            create rule "rule1"
                            when (subject) => pAny()
                            performs (op) => pContainedIn(["e1", "e2"])
                            on (opnd1) => pContainedIn(["oa1", "oa2"])
                            do (evtCtx) {        
                                create PC "pc2"
                            }
                            create rule "rule2"
                            when (subject) => pEquals("u1")
                            performs (op) => pEquals("e3")
                            on (opnd1) => pContainedIn(["oa1", "oa2"])
                            do (evtCtx) {        
                                create PC "pc3"
                            }
                        }""",
                stmt.toFormattedString(0)
        );
    }

}