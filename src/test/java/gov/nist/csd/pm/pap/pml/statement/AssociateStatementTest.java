package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.graph.relationships.Association;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static org.junit.jupiter.api.Assertions.*;

class AssociateStatementTest {

    @Test
    void testSuccess() throws PMException {
        AssociateStatement stmt = new AssociateStatement(
                new StringLiteral("ua1"),
                new StringLiteral("oa1"),
                buildArrayLiteral("read")
        );

        MemoryPolicyStore store = new MemoryPolicyStore();
        store.graph().setResourceAccessRights(new AccessRightSet("read"));
        store.graph().createPolicyClass("pc1", new HashMap<>());
        store.graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        store.graph().createUserAttribute("u1", new HashMap<>(), List.of("pc1"));
        store.graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
        ExecutionContext execCtx = new ExecutionContext(new UserContext("u1"), GlobalScope.forExecute(store));
        stmt.execute(execCtx, store);

        assertTrue(store.graph().getAssociationsWithSource("ua1").get(0).equals(new Association("ua1", "oa1", new AccessRightSet("read"))));
        assertTrue(store.graph().getAssociationsWithTarget("oa1").get(0).equals(new Association("ua1", "oa1", new AccessRightSet("read"))));
    }

    @Test
    void testToFormattedString() {
        AssociateStatement stmt = new AssociateStatement(
                new StringLiteral("ua1"),
                new StringLiteral("oa1"),
                buildArrayLiteral("read")
        );
        assertEquals(
                "associate \"ua1\" and \"oa1\" with [\"read\"]",
                stmt.toFormattedString(0)
        );
        assertEquals(
                "    associate \"ua1\" and \"oa1\" with [\"read\"]",
                stmt.toFormattedString(1)
        );
    }

}