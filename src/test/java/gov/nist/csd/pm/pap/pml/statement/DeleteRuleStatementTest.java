package gov.nist.csd.pm.pap.pml.statement;


import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import org.junit.jupiter.api.Test;

import java.util.*;

import static gov.nist.csd.pm.pap.pml.pattern.AnyPatternFunction.pAny;
import static org.junit.jupiter.api.Assertions.*;

class DeleteRuleStatementTest {

    @Test
    void testSuccess() throws PMException {
        DeleteRuleStatement stmt = new DeleteRuleStatement(
                new StringLiteral("rule1"), new StringLiteral("obl1"));

        PAP pap = new MemoryPAP();
        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
        UserContext userContext = new UserContext("u1");
        pap.modify().obligations().create(userContext, "obl1", new ArrayList<>(List.of(new Rule(
                "rule1",
                new EventPattern(pAny("s"), pAny("o")),
                new Response("e", List.of()))
        )));

        ExecutionContext execCtx = new ExecutionContext(userContext, GlobalScope.forExecute(pap));
        stmt.execute(execCtx, pap);

        assertTrue(pap.query().obligations().get("obl1").getRules().isEmpty());
    }

    @Test
    void testToFormattedString() {
        DeleteRuleStatement stmt = new DeleteRuleStatement(
                new StringLiteral("rule1"), new StringLiteral("obl1"));

        assertEquals(
                """
                        delete rule "rule1" from obligation "obl1"
                        """,
                stmt.toFormattedString(0) + "\n"
        );
        assertEquals(
                """
                            delete rule "rule1" from obligation "obl1"
                        """,
                stmt.toFormattedString(1) + "\n"
        );
    }

}