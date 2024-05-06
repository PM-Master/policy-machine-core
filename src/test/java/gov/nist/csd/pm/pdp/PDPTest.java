package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.exception.BootstrapExistingPolicyException;
import gov.nist.csd.pm.pap.exception.NodeNameExistsException;
import gov.nist.csd.pm.common.exception.PMException;
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
        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        PDP pdp = new PDP(pap);

        pap.runTx(txPAP -> {
            txPAP.policy().graph().createPolicyClass("pc1", new HashMap<>());
            txPAP.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            txPAP.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            txPAP.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            txPAP.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
            txPAP.policy().graph().createObject("o1", new HashMap<>(), List.of("oa1"));
        });

        assertThrows(PMException.class, () -> pdp.runTx(new UserContext("u1"), ((policy) ->
                policy.graph().associate("ua1", "oa1", new AccessRightSet(CREATE_OBJECT_ATTRIBUTE)))));

        assertTrue(pap.policy().graph().nodeExists("pc1"));
        assertTrue(pap.policy().graph().nodeExists("oa1"));
    }


    @Test
    void testBootstrapWithAdminPolicyOnly() throws PMException {
        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        PDP pdp = new PDP(pap);

        pdp.bootstrap(p -> {
            p.policy().graph().createPolicyClass("pc1", new HashMap<>());
        });

        testAdminPolicy(pap, 2);
        assertTrue(pap.policy().graph().nodeExists("pc1"));
        assertTrue(pap.policy().graph().nodeExists(AdminPolicy.policyClassTargetName("pc1")));
    }

    @Test
    void testBootstrapWithExistingPolicyThrowsException() throws PMException {
        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        PDP pdp = new PDP(pap);
        pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
        assertThrows(BootstrapExistingPolicyException.class, () -> {
            pdp.bootstrap((policy) -> {});
        });

        pap.policy().reset();

        pap.policy().graph().setResourceAccessRights(new AccessRightSet("read"));
        pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
        pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
        pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
        pap.policy().graph().createObject("o1", new HashMap<>(), List.of("oa1"));

        pap.policy().prohibitions().create("pro1", new ProhibitionSubject("u1", ProhibitionSubject.Type.USER),
                                  new AccessRightSet("read"), true, new ContainerCondition("oa1", false));

        assertThrows(BootstrapExistingPolicyException.class, () -> {
            pdp.bootstrap((policy) -> {});
        });

        pap.policy().obligations().create(new UserContext("u1"), "obl1");

        assertThrows(BootstrapExistingPolicyException.class, () -> {
            pdp.bootstrap((policy) -> {});
        });
    }

    @Test
    void testRollback() throws PMException {
        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        PDP pdp = new PDP(pap);
        pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
        pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
        pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
        pap.policy().graph().associate("ua1", AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), new AccessRightSet("*"));

        assertThrows(NodeNameExistsException.class, () -> {
            pdp.runTx(new UserContext("u1"), policy -> {
                policy.graph().createPolicyClass("pc2", new HashMap<>());
                // expect error and rollback
                policy.graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc2"));
            });
        });

        assertTrue(pap.policy().graph().nodeExists("pc1"));
        assertTrue(pap.policy().graph().nodeExists("ua1"));
        assertTrue(pap.policy().graph().nodeExists("oa1"));
        assertFalse(pap.policy().graph().nodeExists("pc2"));
    }

    @Test
    void testExecutePML() throws PMException {
        try {
            MemoryPolicyModifier ps = new MemoryPolicyModifier();
            MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
            PAP pap = new PAP(ps, pr);
            PDP pdp = new PDP(pap);
            SamplePolicy.loadSamplePolicyFromPML(pap);

            FunctionDefinitionStatement functionDefinitionStatement = new FunctionDefinitionStatement.Builder("testfunc")
                    .returns(Type.voidType())
                    .args()
                    .executor((ctx, policy) -> {
                        policy.graph().createPolicyClass("pc3", new HashMap<>());
                        return new VoidValue();
                    })
                    .build();

            pdp.runTx(new UserContext("u1"), policy -> {
                policy.pml().createFunction(functionDefinitionStatement);
                policy.executePML(new UserContext("u1"), "create ua \"ua3\" assign to [\"pc2\"]");
            });

            assertTrue(pap.policy().graph().nodeExists("ua3"));

            UnauthorizedException e = assertThrows(UnauthorizedException.class, () -> {
                pdp.runTx(new UserContext("u1"), policy -> {
                    policy.executePML(new UserContext("u1"), "testfunc()");
                });
            });

            assertFalse(pap.policy().graph().nodeExists("pc3"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}