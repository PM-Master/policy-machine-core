package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildMapLiteral;
import static org.junit.jupiter.api.Assertions.*;

class SetNodePropertiesStatementTest {

    @Test
    void testSuccess() throws PMException {
        SetNodePropertiesStatement stmt = new SetNodePropertiesStatement(
                new StringLiteral("ua1"),
                buildMapLiteral("a", "b", "c", "d")
        );

        MemoryPolicyStore store = new MemoryPolicyStore();
        store.graph().createPolicyClass("pc1", new HashMap<>());
        store.graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        store.graph().createUser("u1", new HashMap<>(), List.of("ua1"));
        UserContext userContext = new UserContext("u1");

        stmt.execute(new ExecutionContext(userContext, GlobalScope.forExecute(new MemoryPolicyStore())), store);

        assertEquals(
                Map.of("a", "b", "c", "d"),
                store.graph().getNode("ua1").getProperties()
        );
    }

    @Test
    void testToFormattedString() {
        SetNodePropertiesStatement stmt = new SetNodePropertiesStatement(
                new StringLiteral("ua1"),
                buildMapLiteral("a", "b", "c", "d")
        );

        assertEquals(
                "set properties of \"ua1\" to {\"a\": \"b\", \"c\": \"d\"}",
                stmt.toFormattedString(0)
        );
        assertEquals(
                "    set properties of \"ua1\" to {\"a\": \"b\", \"c\": \"d\"}",
                stmt.toFormattedString(1)
        );
    }

}