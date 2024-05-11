package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.exception.BootstrapExistingPolicyException;
import gov.nist.csd.pm.pap.exception.NodeNameExistsException;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.util.SamplePolicy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.pap.PAPTest.testAdminPolicy;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBJECT_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.*;

class PDPTest {

    @Test
    void testRunTx() throws PMException {
        PAP pap = new MemoryPAP();
        PDP pdp = new PDP(pap);

        pap.runTx(txPAP -> {
            txPAP.modify().graph().createPolicyClass("pc1", new HashMap<>());
            txPAP.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            txPAP.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            txPAP.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            txPAP.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
            txPAP.modify().graph().createObject("o1", new HashMap<>(), List.of("oa1"));
        });

        assertThrows(PMException.class, () -> pdp.runTx(new UserContext("u1"), ((policy) ->
                policy.modify().graph().associate("ua1", "oa1", new AccessRightSet(CREATE_OBJECT_ATTRIBUTE)))));

        assertTrue(pap.query().graph().nodeExists("pc1"));
        assertTrue(pap.query().graph().nodeExists("oa1"));
    }


    @Test
    void testBootstrapWithAdminPolicyOnly() throws PMException {
        PAP pap = new MemoryPAP();
        PDP pdp = new PDP(pap);

        pdp.bootstrap(p -> {
            p.modify().graph().createPolicyClass("pc1", new HashMap<>());
        });

        testAdminPolicy(pap, 2);
        assertTrue(pap.query().graph().nodeExists("pc1"));
        assertTrue(pap.query().graph().nodeExists(AdminPolicy.policyClassTargetName("pc1")));
    }

    @Test
    void testBootstrapWithExistingPolicyThrowsException() throws PMException {
        PAP pap = new MemoryPAP();
        PDP pdp = new PDP(pap);
        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        assertThrows(BootstrapExistingPolicyException.class, () -> {
            pdp.bootstrap((policy) -> {});
        });

        pap.reset();

        pap.modify().graph().setResourceAccessRights(new AccessRightSet("read"));
        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
        pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createObject("o1", new HashMap<>(), List.of("oa1"));

        pap.modify().prohibitions().create("pro1", new ProhibitionSubject("u1", ProhibitionSubject.Type.USER),
                                  new AccessRightSet("read"), true, new ContainerCondition("oa1", false));

        assertThrows(BootstrapExistingPolicyException.class, () -> {
            pdp.bootstrap((policy) -> {});
        });

        pap.modify().obligations().create(new UserContext("u1"), "obl1");

        assertThrows(BootstrapExistingPolicyException.class, () -> {
            pdp.bootstrap((policy) -> {});
        });
    }

    @Test
    void testRollback() throws PMException {
        PAP pap = new MemoryPAP();
        PDP pdp = new PDP(pap);
        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
        pap.modify().graph().associate("ua1", AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), new AccessRightSet("*"));

        assertThrows(NodeNameExistsException.class, () -> {
            pdp.runTx(new UserContext("u1"), policy -> {
                policy.modify().graph().createPolicyClass("pc2", new HashMap<>());
                // expect error and rollback
                policy.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc2"));
            });
        });

        assertTrue(pap.query().graph().nodeExists("pc1"));
        assertTrue(pap.query().graph().nodeExists("ua1"));
        assertTrue(pap.query().graph().nodeExists("oa1"));
        assertFalse(pap.query().graph().nodeExists("pc2"));
    }

    @Test
    void testExecutePML() throws PMException {
        try {
            PAP pap = new MemoryPAP();
            PDP pdp = new PDP(pap);
            SamplePolicy.loadSamplePolicyFromPML(pap);

            FunctionDefinitionStatement functionDefinitionStatement = new FunctionDefinitionStatement.Builder("testfunc")
                    .returns(Type.voidType())
                    .args()
                    .executor((ctx, policy) -> {
                        policy.modify().graph().createPolicyClass("pc3", new HashMap<>());
                        return new VoidValue();
                    })
                    .build();

            pdp.runTx(new UserContext("u1"), policy -> {
                policy.modify().pml().createFunction(functionDefinitionStatement);
                policy.executePML(new UserContext("u1"), "create ua \"ua3\" assign to [\"pc2\"]");
            });

            assertTrue(pap.query().graph().nodeExists("ua3"));

            UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> {
                pdp.runTx(new UserContext("u1"), policy -> {
                    policy.executePML(new UserContext("u1"), "testfunc()");
                });
            });

            assertFalse(pap.query().graph().nodeExists("pc3"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}