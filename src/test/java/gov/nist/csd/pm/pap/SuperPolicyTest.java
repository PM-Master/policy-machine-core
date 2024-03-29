package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.pap.memory.MemoryPolicyStore;
import gov.nist.csd.pm.pdp.memory.MemoryPolicyReviewer;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.AccessRightSet;
import gov.nist.csd.pm.policy.model.access.UserContext;
import gov.nist.csd.pm.policy.model.graph.relationships.Association;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static gov.nist.csd.pm.pap.SuperPolicy.*;
import static gov.nist.csd.pm.policy.model.access.AdminAccessRights.allAccessRights;
import static gov.nist.csd.pm.policy.model.access.AdminAccessRights.allAdminAccessRights;
import static org.junit.jupiter.api.Assertions.*;

class SuperPolicyTest {

    static PAP pap;
    @BeforeAll
    static void init() throws PMException {
        pap = new PAP(new MemoryPolicyStore());
        SuperPolicy.verifySuperPolicy(pap.policyStore);
    }

    @Test
    void verifySuperPC() throws PMException {
        assertTrue(pap.graph().nodeExists(SUPER_PC));

        List<String> expected = List.of(SUPER_UA, SUPER_UA1, SUPER_OA);
        List<String> actual = pap.graph().getChildren(SUPER_PC);
        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));

        assertTrue(pap.graph().nodeExists(SUPER_PC_REP));

        expected = List.of(SUPER_OA);
        assertEquals(expected, pap.graph().getParents(SUPER_PC_REP));
    }

    @Test
    void verifySuperUA() throws PMException {
        assertTrue(pap.graph().nodeExists(SUPER_UA));

        List<String> actual = pap.graph().getParents(SUPER_UA);
        List<String> expected = List.of(SUPER_PC);
        assertEquals(expected, actual);

        actual = pap.graph().getChildren(SUPER_UA);
        expected = List.of(SUPER_USER);
        assertEquals(expected, actual);

        List<Association> expectedAssocs = List.of(
                new Association(SUPER_UA, SUPER_OA, allAccessRights())
        );
        List<Association> actualAssocs = pap.graph().getAssociationsWithSource(SUPER_UA);
        assertEquals(expectedAssocs, actualAssocs);

        expectedAssocs = List.of(new Association(SUPER_UA, SUPER_OA, allAccessRights()));
        actualAssocs = pap.graph().getAssociationsWithTarget(SUPER_OA);
        assertEquals(expectedAssocs, actualAssocs);
    }

    @Test
    void verifySuperUA1() throws PMException {
        assertTrue(pap.graph().nodeExists(SUPER_UA1));

        List<String> actual = pap.graph().getParents(SUPER_UA1);
        List<String> expected = List.of(SUPER_PC);
        assertEquals(expected, actual);

        actual = pap.graph().getChildren(SUPER_UA1);
        expected = List.of(SUPER_USER);
        assertEquals(expected, actual);

        List<Association> expectedAssocs = List.of(new Association(SUPER_UA1, SUPER_UA, allAccessRights()));
        List<Association> actualAssocs = pap.graph().getAssociationsWithSource(SUPER_UA1);
        assertEquals(expectedAssocs, actualAssocs);

        expectedAssocs = List.of(new Association(SUPER_UA1, SUPER_UA, allAccessRights()));
        actualAssocs = pap.graph().getAssociationsWithTarget(SUPER_UA);
        assertEquals(expectedAssocs, actualAssocs);
    }

    @Test
    void verifySuperOA() throws PMException {
        assertTrue(pap.graph().nodeExists(SUPER_OA));

        List<String> actual = pap.graph().getParents(SUPER_OA);
        List<String> expected = List.of(SUPER_PC);
        assertEquals(expected, actual);

        actual = pap.graph().getChildren(SUPER_OA);
        expected = List.of(SUPER_PC_REP);
        assertEquals(expected, actual);

        List<Association> expectedAssocs = List.of(new Association(SUPER_UA, SUPER_OA, allAccessRights()));
        List<Association> actualAssocs = pap.graph().getAssociationsWithTarget(SUPER_OA);
        assertEquals(expectedAssocs, actualAssocs);
    }

    @Test
    void verifySuperUser() throws PMException {
        assertTrue(pap.graph().nodeExists(SUPER_USER));

        List<String> actual = pap.graph().getParents(SUPER_USER);
        List<String> expected = List.of(SUPER_UA, SUPER_UA1);
        assertEquals(expected, actual);
    }

    @Nested
    class FixSuperPolicy {

        @Test
        void testNodes() throws PMException {
            MemoryPolicyStore policy = new MemoryPolicyStore();
            PAP pap = new PAP(policy);
            verifySuperPolicy(pap.policyStore);
            assertTrue(pap.graph().nodeExists(SUPER_PC));
            assertTrue(pap.graph().nodeExists(SUPER_UA));
            assertTrue(pap.graph().nodeExists(SUPER_UA1));
            assertTrue(pap.graph().nodeExists(SUPER_OA));
            assertTrue(pap.graph().nodeExists(SUPER_PC_REP));
            assertTrue(pap.graph().nodeExists(SUPER_USER));
        }

        @Test
        void testSuperUARelations() throws PMException {
            PolicyStore policy = new MemoryPolicyStore();
            policy.graph().createPolicyClass(SUPER_PC);
            policy.graph().createUserAttribute("test", SUPER_PC);
            policy.graph().createUserAttribute(SUPER_UA, "test");

            PAP pap = new PAP(policy);
            verifySuperPolicy(pap.policyStore);

            assertTrue(pap.graph().getParents(SUPER_UA).contains(SUPER_PC));
            assertTrue(pap.graph().getAssociationsWithTarget(SUPER_UA).contains(new Association(SUPER_UA1, SUPER_UA)));
        }

        @Test
        void testSuperUA1Relations() throws PMException {
            PolicyStore policy = new MemoryPolicyStore();
            policy.graph().createPolicyClass(SUPER_PC);
            policy.graph().createUserAttribute("test", SUPER_PC);
            policy.graph().createUserAttribute(SUPER_UA1, "test");

            PAP pap = new PAP(policy);
            verifySuperPolicy(pap.policyStore);

            assertTrue(pap.graph().getParents(SUPER_UA1).contains(SUPER_PC));
        }

        @Test
        void testSuperOARelations() throws PMException {
            PolicyStore policy = new MemoryPolicyStore();
            policy.graph().createPolicyClass(SUPER_PC);
            policy.graph().createObjectAttribute("test", SUPER_PC);
            policy.graph().createObjectAttribute(SUPER_OA, "test");

            PAP pap = new PAP(policy);
            verifySuperPolicy(pap.policyStore);

            assertTrue(pap.graph().getParents(SUPER_OA).contains(SUPER_PC));
            assertTrue(pap.graph().getAssociationsWithTarget(SUPER_OA).contains(new Association(SUPER_UA, SUPER_OA, allAccessRights())));
        }

        @Test
        void testSuperUserAssignments() throws PMException {
            MemoryPolicyStore policy = new MemoryPolicyStore();

            PAP pap = new PAP(policy);
            verifySuperPolicy(pap.policyStore);

            assertTrue(pap.graph().getParents(SUPER_USER).containsAll(List.of(SUPER_UA, SUPER_UA1)));
        }

        @Test
        void testSuperPCRepAssignments() throws PMException {
            MemoryPolicyStore policy = new MemoryPolicyStore();

            PAP pap = new PAP(policy);
            verifySuperPolicy(pap.policyStore);

            assertTrue(pap.graph().getParents(SUPER_PC_REP).contains(SUPER_OA));
        }

    }

    @Test
    void testExistingPCs() throws PMException {
        PolicyStore store = new MemoryPolicyStore();
        store.graph().createPolicyClass("pc1");
        store.graph().createUserAttribute("ua1", "pc1");
        store.graph().createObjectAttribute("oa1", "pc1");

        PAP pap = new PAP(store);
        SuperPolicy.verifySuperPolicy(pap.policyStore);

        assertTrue(pap.graph().getAssociationsWithSource(SUPER_UA).containsAll(List.of(
                new Association(SUPER_UA, "ua1", allAccessRights()),
                new Association(SUPER_UA, "oa1", allAccessRights())
        )));
    }

    @Test
    void testApplySuperPolicy() throws PMException {
        PAP pap = new PAP(new MemoryPolicyStore());
        MemoryPolicyReviewer reviewer = new MemoryPolicyReviewer(pap);
        UserContext userContext = new UserContext(SUPER_USER);
        AccessRightSet accessRights = reviewer.getAccessRights(userContext, SUPER_USER);
        assertTrue(accessRights.containsAll(allAdminAccessRights()));
        accessRights = reviewer.getAccessRights(userContext, SUPER_UA);
        assertTrue(accessRights.containsAll(allAdminAccessRights()));
        accessRights = reviewer.getAccessRights(userContext, SUPER_PC);
        assertFalse(accessRights.containsAll(allAdminAccessRights()));
        accessRights = reviewer.getAccessRights(userContext, SUPER_PC_REP);
        assertTrue(accessRights.containsAll(allAdminAccessRights()));

        pap.graph().createPolicyClass("pc1");
        accessRights = reviewer.getAccessRights(userContext, SuperPolicy.pcRepObjectAttribute("pc1"));
        assertTrue(accessRights.containsAll(allAdminAccessRights()));
    }
}