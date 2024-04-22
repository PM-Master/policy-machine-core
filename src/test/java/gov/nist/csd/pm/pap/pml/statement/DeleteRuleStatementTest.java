package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.event.EventPattern;
import gov.nist.csd.pm.common.obligation.event.Performs;
import gov.nist.csd.pm.common.obligation.event.subject.AnyUserSubject;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeleteRuleStatementTest {

    @Test
    void testSuccess() throws PMException {
        DeleteRuleStatement stmt = new DeleteRuleStatement(
                new StringLiteral("rule1"), new StringLiteral("obl1"));

        MemoryPolicyStore store = new MemoryPolicyStore();
        store.graph().createPolicyClass("pc1", new HashMap<>());
        store.graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        store.graph().createUser("u1", new HashMap<>(), List.of("ua1"));
        UserContext userContext = new UserContext("u1");
        store.obligations().create(userContext, "obl1", new Rule(
                "rule1",
                new EventPattern(new AnyUserSubject(), new Performs("e1")),
                new Response("e",List.of())
        ));

        ExecutionContext execCtx = new ExecutionContext(userContext, GlobalScope.withValuesAndDefinitions(store));
        stmt.execute(execCtx, store);

        assertTrue(store.obligations().get("obl1").getRules().isEmpty());
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