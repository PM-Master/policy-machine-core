package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.exception.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.ALL_ADMIN_ACCESS_RIGHTS;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_POLICY_CLASS;
import static org.junit.jupiter.api.Assertions.*;

public abstract class ProhibitionsModifierTest extends ModificationTest {

    @Nested
    class CreateProhibitionTest {

        @Test
        void testProhibitionExistsException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));

            pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet(), false, List.of());

            assertThrows(
                    ProhibitionExistsException.class,
                    () -> pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet(), false, List.of()));
        }

        @Test
        void testProhibitionSubjectDoesNotExistException() {
            assertThrows(
                    ProhibitionSubjectDoesNotExistException.class,
                    () -> pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet(ALL_ADMIN_ACCESS_RIGHTS), false, List.of()));
        }


        @Test
        void testUnknownAccessRightException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));

            assertThrows(
                    UnknownAccessRightException.class,
                    () -> pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), false, List.of()));
        }

        @Test
        void testProhibitionContainerDoesNotExistException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read"));
            assertThrows(
                    ProhibitionContainerDoesNotExistException.class,
                    () -> pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), false,
                            Collections.singleton(new ContainerCondition("oa1", true))
                    ));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

            pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    List.of(
                    new ContainerCondition("oa1", true),
                    new ContainerCondition("oa2", false)
                    )
            );

            Prohibition p = pap.query().prohibitions().get("pro1");
            assertEquals("pro1", p.getName());
            assertEquals("subject", p.getSubject().getName());
            assertEquals(new AccessRightSet("read"), p.getAccessRightSet());
            assertTrue(p.isIntersection());
            assertEquals(2, p.getContainers().size());
            List<ContainerCondition> expected = List.of(
                    new ContainerCondition("oa1", true),
                    new ContainerCondition("oa2", false)
            );
            assertTrue(expected.containsAll(p.getContainers()) && p.getContainers().containsAll(expected));
        }
    }

    @Nested
    class UpdateProhibitionTest {

        @Test
        void testProhibitionDoesNotExistException() throws PMException {
            pap.modify().graph().createPolicyClass("pc", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua", new HashMap<>(), List.of("pc"));

            assertThrows(
                    ProhibitionDoesNotExistException.class,
                    () -> pap.modify().prohibitions().update("pro1", ProhibitionSubject.userAttribute("ua"), new AccessRightSet(
                            CREATE_POLICY_CLASS), false, List.of()));
        }


        @Test
        void testUnknownAccessRightException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

            pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    Collections.singleton(new ContainerCondition("oa1", true))
            );

            assertThrows(UnknownAccessRightException.class,
                    () -> pap.modify().prohibitions().update("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("test"), false, List.of()));
        }

        @Test
        void testProhibitionSubjectDoesNotExistException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

            pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    Collections.singleton(new ContainerCondition("oa1", true))
            );

            assertThrows(ProhibitionSubjectDoesNotExistException.class,
                    () -> pap.modify().prohibitions().update("pro1", ProhibitionSubject.userAttribute("test"), new AccessRightSet("read"), false, List.of()));
            assertDoesNotThrow(() -> pap.modify().prohibitions().update("pro1", ProhibitionSubject.process("subject"), new AccessRightSet("read"), false, List.of()));
        }

        @Test
        void testProhibitionContainerDoesNotExistException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

            pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    Collections.singleton(new ContainerCondition("oa1", true))
            );

            assertThrows(ProhibitionContainerDoesNotExistException.class,
                    () -> pap.modify().prohibitions().update("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), false,
                            Collections.singleton(new ContainerCondition("oa3", true))
                    ));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("subject2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

            pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    List.of(
                    new ContainerCondition("oa1", true),
                    new ContainerCondition("oa2", false)));
            pap.modify().prohibitions().update("pro1", ProhibitionSubject.userAttribute("subject2"), new AccessRightSet("read", "write"), true,
                    List.of(
                    new ContainerCondition("oa1", false),
                    new ContainerCondition("oa2", true)));

            Prohibition p = pap.query().prohibitions().get("pro1");
            assertEquals("pro1", p.getName());
            assertEquals("subject2", p.getSubject().getName());
            assertEquals(new AccessRightSet("read", "write"), p.getAccessRightSet());
            assertTrue(p.isIntersection());
            assertEquals(2, p.getContainers().size());
            List<ContainerCondition> expected = List.of(
                    new ContainerCondition("oa1", false),
                    new ContainerCondition("oa2", true)
            );
            assertTrue(expected.containsAll(p.getContainers()) && p.getContainers().containsAll(expected));
        }
    }

    @Nested
    class DeleteProhibitionTest {

        @Test
        void testNonExistingProhibitionDoesNotThrowException() {
            assertDoesNotThrow(() -> pap.modify().prohibitions().delete("pro1"));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

            pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    List.of(
                    new ContainerCondition("oa1", true),
                    new ContainerCondition("oa2", false)));

            assertDoesNotThrow(() -> pap.query().prohibitions().get("pro1"));

            pap.modify().prohibitions().delete("pro1");

            assertThrows(ProhibitionDoesNotExistException.class,
                    () -> pap.query().prohibitions().get("pro1"));
        }
    }

    @Nested
    class GetAll {

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa4", new HashMap<>(), List.of("pc1"));

            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

            pap.modify().prohibitions().create("label1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    List.of(
                    new ContainerCondition("oa1", true),
                    new ContainerCondition("oa2", false)));
            pap.modify().prohibitions().create("label2", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    List.of(
                    new ContainerCondition("oa3", true),
                    new ContainerCondition("oa4", false)));

            Map<String, Collection<Prohibition>> prohibitions = pap.query().prohibitions().getAll();
            assertEquals(1, prohibitions.size());
            assertEquals(2, prohibitions.get("subject").size());
            checkProhibitions(prohibitions.get("subject"));
        }

        private void checkProhibitions(Collection<Prohibition> prohibitions) {
            for (Prohibition p : prohibitions) {
                if (p.getName().equals("label1")) {
                    assertEquals("label1", p.getName());
                    assertEquals("subject", p.getSubject().getName());
                    assertEquals(new AccessRightSet("read"), p.getAccessRightSet());
                    assertTrue(p.isIntersection());
                    assertEquals(2, p.getContainers().size());
                    List<ContainerCondition> expected = List.of(
                            new ContainerCondition("oa1", true),
                            new ContainerCondition("oa2", false)
                    );
                    assertTrue(expected.containsAll(p.getContainers()) && p.getContainers().containsAll(expected));
                } else if (p.getName().equals("label2")) {
                    assertEquals("label2", p.getName());
                    assertEquals("subject", p.getSubject().getName());
                    assertEquals(new AccessRightSet("read"), p.getAccessRightSet());
                    assertTrue(p.isIntersection());
                    assertEquals(2, p.getContainers().size());
                    List<ContainerCondition> expected = List.of(
                            new ContainerCondition("oa3", true),
                            new ContainerCondition("oa4", false)
                    );
                    assertTrue(expected.containsAll(p.getContainers()) && p.getContainers().containsAll(expected));
                } else {
                    fail("unexpected prohibition label " + p.getName());
                }
            }
        }
    }

    @Nested
    class GetWithSubject {

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("subject2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa4", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

            pap.modify().prohibitions().create("label1", ProhibitionSubject.userAttribute("subject1"), new AccessRightSet("read"), true,
                    List.of(new ContainerCondition("oa1", true),
                    new ContainerCondition("oa2", false)));
            pap.modify().prohibitions().create("label2", ProhibitionSubject.userAttribute("subject2"), new AccessRightSet("read"), true,
                    List.of(new ContainerCondition("oa3", true),
                    new ContainerCondition("oa4", false)));

            Collection<Prohibition> pros = pap.query().prohibitions().getWithSubject("subject1");
            assertEquals(1, pros.size());

            Prohibition p = pros.iterator().next();

            assertEquals("label1", p.getName());
            assertEquals("subject1", p.getSubject().getName());
            assertEquals(new AccessRightSet("read"), p.getAccessRightSet());
            assertTrue(p.isIntersection());
            assertEquals(2, p.getContainers().size());
            List<ContainerCondition> expected = List.of(
                    new ContainerCondition("oa1", true),
                    new ContainerCondition("oa2", false)
            );
            assertTrue(expected.containsAll(p.getContainers()) && p.getContainers().containsAll(expected));
        }

    }

    @Nested
    class Get {

        @Test
        void testSuccess() throws PMException {
            assertThrows(ProhibitionDoesNotExistException.class,
                    () -> pap.query().prohibitions().get("pro1"));

            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa4", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

            pap.modify().prohibitions().create("label1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    List.of(new ContainerCondition("oa1", true),
                    new ContainerCondition("oa2", false)));
            pap.modify().prohibitions().create("label2", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                    List.of(new ContainerCondition("oa3", true),
                    new ContainerCondition("oa4", false)));

            Prohibition p = pap.query().prohibitions().get("label1");
            assertEquals("label1", p.getName());
            assertEquals("subject", p.getSubject().getName());
            assertEquals(new AccessRightSet("read"), p.getAccessRightSet());
            assertTrue(p.isIntersection());
            assertEquals(2, p.getContainers().size());
            List<ContainerCondition> expected = List.of(
                    new ContainerCondition("oa1", true),
                    new ContainerCondition("oa2", false)
            );
            assertTrue(expected.containsAll(p.getContainers()) && p.getContainers().containsAll(expected));
        }
    }
}