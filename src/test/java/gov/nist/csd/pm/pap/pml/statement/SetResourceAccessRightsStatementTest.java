package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import org.junit.jupiter.api.Test;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.*;

class SetResourceAccessRightsStatementTest {

    @Test
    void testSuccess() throws PMException {
        SetResourceAccessRightsStatement stmt = new SetResourceAccessRightsStatement(
                buildArrayLiteral("a", "b", "c", "d")
        );

        MemoryPolicyStore store = new MemoryPolicyStore();

        stmt.execute(new ExecutionContext(new UserContext(""), GlobalScope.forExecute(new MemoryPolicyStore())), store);

        assertEquals(
                new AccessRightSet("a", "b", "c", "d"),
                store.graph().getResourceAccessRights()
        );
    }

    @Test
    void testToFormattedString() {
        SetResourceAccessRightsStatement stmt = new SetResourceAccessRightsStatement(
                buildArrayLiteral("a", "b", "c", "d")
        );

        assertEquals(
                "set resource access rights [\"a\", \"b\", \"c\", \"d\"]",
                stmt.toFormattedString(0)
        );
        assertEquals(
                "    set resource access rights [\"a\", \"b\", \"c\", \"d\"]",
                stmt.toFormattedString(1)
        );
    }

}