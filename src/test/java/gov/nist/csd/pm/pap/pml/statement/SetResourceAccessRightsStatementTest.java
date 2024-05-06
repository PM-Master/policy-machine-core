package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
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

        MemoryPolicyModifier store = new MemoryPolicyModifier();

        stmt.execute(new ExecutionContext(new UserContext(""), GlobalScope.forExecute(new MemoryPolicyModifier())), store);

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