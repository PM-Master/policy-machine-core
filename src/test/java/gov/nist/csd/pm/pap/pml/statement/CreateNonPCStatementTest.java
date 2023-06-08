package gov.nist.csd.pm.pap.pml.statement;


import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.pml.PMLUtil.buildArrayLiteral;
import static gov.nist.csd.pm.pap.pml.PMLUtil.buildMapLiteral;
import static org.junit.jupiter.api.Assertions.*;

class CreateNonPCStatementTest {

    @Test
    void testSuccess() throws PMException {
        CreateNonPCStatement stmt1 = new CreateNonPCStatement(new StringLiteral("ua1"), NodeType.UA, buildArrayLiteral("pc1"));
        CreateNonPCStatement stmt2 = new CreateNonPCStatement(new StringLiteral("oa1"), NodeType.OA, buildArrayLiteral("pc1"));
        CreateNonPCStatement stmt3 = new CreateNonPCStatement(new StringLiteral("u1"), NodeType.U, buildArrayLiteral("ua1"));
        CreateNonPCStatement stmt4 = new CreateNonPCStatement(new StringLiteral("o1"), NodeType.O, buildArrayLiteral("oa1"));

        PAP pap = new MemoryPAP();
        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createUser("u2", new HashMap<>(), List.of("ua2"));
        ExecutionContext execCtx = new ExecutionContext(new UserContext("u2"), GlobalScope.forExecute(new MemoryPAP()));

        stmt1.execute(execCtx, pap);
        stmt2.execute(execCtx, pap);
        stmt3.execute(execCtx, pap);
        stmt4.execute(execCtx, pap);

        assertTrue(pap.query().graph().nodeExists("ua1"));
        assertTrue(pap.query().graph().nodeExists("oa1"));
        assertTrue(pap.query().graph().nodeExists("u1"));
        assertTrue(pap.query().graph().nodeExists("o1"));
        
        assertTrue(pap.query().graph().getParents("ua1").contains("pc1"));
        assertTrue(pap.query().graph().getParents("oa1").contains("pc1"));
        assertTrue(pap.query().graph().getParents("u1").contains("ua1"));
        assertTrue(pap.query().graph().getParents("o1").contains("oa1"));
    }

    @Test
    void testWithProperties() throws PMException {
        CreateNonPCStatement stmt1 = new CreateNonPCStatement(new StringLiteral("ua1"), NodeType.UA, buildArrayLiteral("pc1"),
                                                              buildMapLiteral("a", "b", "c", "d"));

        PAP pap = new MemoryPAP();
        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua2"));
        ExecutionContext execCtx = new ExecutionContext(new UserContext("u1"), GlobalScope.forExecute(new MemoryPAP()));

        stmt1.execute(execCtx, pap);

       assertEquals(Map.of("a", "b", "c", "d"), pap.query().graph().getNode("ua1").getProperties());
    }

    @Test
    void testToFormattedString() {
        CreateNonPCStatement stmt = new CreateNonPCStatement(
                new StringLiteral("ua1"),
                NodeType.UA,
                buildArrayLiteral("ua2"),
                buildMapLiteral("a", "b")
        );
        assertEquals(
                "create UA \"ua1\" with properties {\"a\": \"b\"} assign to [\"ua2\"]",
                stmt.toFormattedString(0)
        );
        assertEquals(
                "    create UA \"ua1\" with properties {\"a\": \"b\"} assign to [\"ua2\"]",
                stmt.toFormattedString(1)
        );
    }

}