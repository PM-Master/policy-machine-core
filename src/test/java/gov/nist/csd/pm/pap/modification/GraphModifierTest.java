package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.node.Properties;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.graph.relationship.InvalidAssignmentException;
import gov.nist.csd.pm.common.graph.relationship.InvalidAssociationException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.query.UserContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;
import static gov.nist.csd.pm.common.graph.node.Properties.NO_PROPERTIES;
import static gov.nist.csd.pm.common.graph.node.Properties.toProperties;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.ALL_ADMIN_ACCESS_RIGHTS;
import static gov.nist.csd.pm.pap.pml.pattern.AscendantOfPatternFunction.pAscendantOf;
import static gov.nist.csd.pm.pap.pml.pattern.EqualsPatternFunction.pEquals;
import static org.junit.jupiter.api.Assertions.*;

public abstract class GraphModifierTest extends ModificationTest {

    @Nested
    class SetResourceAccessRights {

        @Test
        void testAdminAccessRightExistsException() {
            assertThrows(AdminAccessRightExistsException.class, () ->
                    pap.modify().graph().setResourceAccessRights(new AccessRightSet(CREATE_POLICY_CLASS)));
        }

        @Test
        void testSuccess() throws PMException {
            AccessRightSet arset = new AccessRightSet("read", "write");
            pap.modify().graph().setResourceAccessRights(arset);
            assertEquals(arset, pap.query().graph().getResourceAccessRights());
        }

    }

    @Nested
    class GetResourceAccessRights {
        @Test
        void testGetResourceAccessRights() throws PMException {
            AccessRightSet arset = new AccessRightSet("read", "write");
            pap.modify().graph().setResourceAccessRights(arset);
            assertEquals(arset, pap.query().graph().getResourceAccessRights());
        }
    }

    @Nested
    class CreatePolicyModificationClassTest {
        @Test
        void testNodeNameExistsException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            assertDoesNotThrow(() -> pap.modify().graph().createPolicyClass("pc2", new HashMap<>()));
            assertThrows(NodeNameExistsException.class, () -> pap.modify().graph().createPolicyClass("pc1", new HashMap<>()));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            String rep = AdminPolicy.policyClassTargetName("pc1");
            assertTrue(pap.query().graph().nodeExists("pc1"));
            assertTrue(pap.query().graph().nodeExists(rep));
            assertTrue(pap.query().graph().getParents(rep).contains(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName()));
            assertTrue(pap.query().graph().getChildren(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName()).contains(rep));
        }
    }

    @Nested
    class CreateObjectAttribute {

        @Test
        void testNodeNameExistsException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            assertThrows(NodeNameExistsException.class,
                    () -> pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1")));
        }

        @Test
        void testNodeDoesNotExistException() throws PMException {
            assertThrows(
                    NodeDoesNotExistException.class,
                    () -> pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1")));

            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1", "pc2")));
        }

        @Test
        void testInvalidAssignmentException()
                throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

            assertThrows(
                    InvalidAssignmentException.class,
                    () -> pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("ua1")));
        }

        @Test
        void testAssignmentCausesLoopException()
                throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("oa1"));

            assertThrows(
                    AssignmentCausesLoopException.class,
                    () -> pap.modify().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("oa3")));
            assertThrows(AssignmentCausesLoopException.class,
                    () -> pap.modify().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("oa2", "oa3")));
        }

        @Test
        void Success() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", toProperties("k", "v"), List.of("oa1"));

            assertTrue(pap.query().graph().nodeExists("oa1"));
            assertTrue(pap.query().graph().nodeExists("oa2"));
            assertEquals("v", pap.query().graph().getNode("oa2").getProperties().get("k"));

            assertTrue(pap.query().graph().getChildren("pc1").contains("oa1"));
            assertTrue(pap.query().graph().getChildren("oa1").contains("oa2"));

            assertTrue(pap.query().graph().getParents("oa1").contains("pc1"));
            assertTrue(pap.query().graph().getParents("oa2").contains("oa1"));
        }

        @Test
        void testNoParents() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            assertThrows(DisconnectedNodeException.class, () -> pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of()));
        }
    }

    @Nested
    class CreateUserAttributeTest {

        @Test
        void testNodeNameExistsException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            assertThrows(NodeNameExistsException.class,
                    () -> pap.modify().graph().createObjectAttribute("ua1", new HashMap<>(), List.of("pc1")));
        }

        @Test
        void testNodeDoesNotExistException() throws PMException {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1")));

            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1", "pc2")));
        }

        @Test
        void testInvalidAssignmentException()
                throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            assertThrows(InvalidAssignmentException.class,
                    () -> pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("oa1")));
        }

        @Test
        void testAssignmentCausesLoopException()
                throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("ua1"));

            assertThrows(AssignmentCausesLoopException.class,
                    () -> pap.modify().graph().createUserAttribute("ua3", new HashMap<>(), List.of("ua3")));
            assertThrows(AssignmentCausesLoopException.class,
                    () -> pap.modify().graph().createUserAttribute("ua3", new HashMap<>(), List.of("ua2", "ua3")));
        }

        @Test
        void Success() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of());
            pap.modify().graph().createUserAttribute("ua2", toProperties("k", "v"), List.of("ua1"));

            assertTrue(pap.query().graph().nodeExists("ua1"));
            assertTrue(pap.query().graph().nodeExists("ua2"));
            assertEquals("v", pap.query().graph().getNode("ua2").getProperties().get("k"));

            assertTrue(pap.query().graph().getChildren("pc1").isEmpty());
            assertTrue(pap.query().graph().getParents("ua1").isEmpty());

            assertTrue(pap.query().graph().getChildren("ua1").contains("ua2"));
            assertTrue(pap.query().graph().getParents("ua2").contains("ua1"));
        }
    }

    @Nested
    class CreateObjectTest {

        @Test
        void testNodeNameExistsException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObject("o1", new HashMap<>(), List.of("oa1"));
            assertThrows(NodeNameExistsException.class,
                    () -> pap.modify().graph().createObject("o1", new HashMap<>(), List.of("oa1")));
        }

        @Test
        void testNodeDoesNotExistException() throws PMException {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().createObject("o1", new HashMap<>(), List.of("oa1")));

            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().createObjectAttribute("o1", new HashMap<>(), List.of("oa1", "oa2")));
        }

        @Test
        void testInvalidAssignmentException()
                throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

            assertThrows(InvalidAssignmentException.class,
                    () -> pap.modify().graph().createObjectAttribute("o1", new HashMap<>(), List.of("ua1")));
        }

        @Test
        void testAssignmentCausesLoopException()
                throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            assertThrows(AssignmentCausesLoopException.class,
                    () -> pap.modify().graph().createObject("o1", new HashMap<>(), List.of("o1")));
            assertThrows(AssignmentCausesLoopException.class,
                    () -> pap.modify().graph().createObject("o1", new HashMap<>(), List.of("oa1", "o1")));
        }

        @Test
        void Success() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            pap.modify().graph().createObject("o1", toProperties("k", "v"), List.of("oa1"));

            assertTrue(pap.query().graph().nodeExists("o1"));
            assertEquals("v", pap.query().graph().getNode("o1").getProperties().get("k"));

            assertTrue(pap.query().graph().getChildren("oa1").contains("o1"));
            assertEquals( List.of("oa1"), pap.query().graph().getParents("o1"));
            assertTrue(pap.query().graph().getChildren("oa1").contains("o1"));
        }
    }

    @Nested
    class CreateUserTest {

        @Test
        void testNodeNameExistsException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
            assertThrows(NodeNameExistsException.class,
                    () -> pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1")));
        }

        @Test
        void testNodeDoesNotExistException() throws PMException {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1")));

            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1", "ua2")));
        }

        @Test
        void testInvalidAssignmentException()
                throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            assertThrows(InvalidAssignmentException.class,
                    () -> pap.modify().graph().createUser("u1", new HashMap<>(), List.of("oa1")));
        }

        @Test
        void testAssignmentCausesLoopException()
                throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

            assertThrows(AssignmentCausesLoopException.class,
                    () -> pap.modify().graph().createUser("u1", new HashMap<>(), List.of("u1")));
            assertThrows(AssignmentCausesLoopException.class,
                    () -> pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1", "u1")));
        }

        @Test
        void Success() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

            pap.modify().graph().createUser("u1", toProperties("k", "v"), List.of("ua1"));

            assertTrue(pap.query().graph().nodeExists("u1"));
            assertEquals("v", pap.query().graph().getNode("u1").getProperties().get("k"));

            assertTrue(pap.query().graph().getChildren("ua1").contains("u1"));
            assertEquals( List.of("ua1"), pap.query().graph().getParents("u1"));
            assertTrue(pap.query().graph().getChildren("ua1").contains("u1"));
        }
    }

    @Nested
    class SetNodePropertiesTest {

        @Test
        void testNodeDoesNotExistException() {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().setNodeProperties("oa1", NO_PROPERTIES));
        }

        @Test
        void testSuccessEmptyProperties() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().setNodeProperties("pc1", NO_PROPERTIES);

            assertTrue(pap.query().graph().getNode("pc1").getProperties().isEmpty());
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().setNodeProperties("pc1", toProperties("k", "v"));

            assertEquals("v", pap.query().graph().getNode("pc1").getProperties().get("k"));
        }
    }

    @Nested
    class NodeExists {
        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            assertTrue(pap.query().graph().nodeExists("pc1"));
            assertTrue(pap.query().graph().nodeExists("ua1"));
            assertFalse(pap.query().graph().nodeExists("pc2"));
        }
    }

    @Nested
    class GetNodeTest {

        @Test
        void testNodeDoesNotExistException() {
            assertThrows(NodeDoesNotExistException.class, () -> pap.query().graph().getNode("pc1"));
        }

        @Test
        void testSuccessPolicyClass() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", Properties.toProperties("k", "v"));

            Node pc1 = pap.query().graph().getNode("pc1");

            assertEquals("pc1", pc1.getName());
            assertEquals(PC, pc1.getType());
            assertEquals("v", pc1.getProperties().get("k"));
        }

        @Test
        void testSuccessObjectAttribute() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", Properties.toProperties("k", "v"), List.of("pc1"));

            Node oa1 = pap.query().graph().getNode("oa1");

            assertEquals("oa1", oa1.getName());
            assertEquals(OA, oa1.getType());
            assertEquals("v", oa1.getProperties().get("k"));
        }
    }

    @Nested
    class Search {
        @Test
        void testSearch() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", toProperties("namespace", "test"), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", toProperties("key1", "value1"), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa3", toProperties("key1", "value1", "key2", "value2"), List.of("pc1"));

            List<String> nodes = pap.query().graph().search(OA, NO_PROPERTIES);
            assertEquals(10, nodes.size());

            nodes = pap.query().graph().search(ANY, toProperties("key1", "value1"));
            assertEquals(2, nodes.size());

            nodes = pap.query().graph().search(ANY, toProperties("namespace", "test"));
            assertEquals(1, nodes.size());

            nodes = pap.query().graph().search(OA, toProperties("namespace", "test"));
            assertEquals(1, nodes.size());
            nodes = pap.query().graph().search(OA, toProperties("key1", "value1"));
            assertEquals(2, nodes.size());
            nodes = pap.query().graph().search(OA, toProperties("key1", "*"));
            assertEquals(2, nodes.size());
            nodes = pap.query().graph().search(OA, toProperties("key1", "value1", "key2", "value2"));
            assertEquals(1, nodes.size());
            nodes = pap.query().graph().search(OA, toProperties("key1", "value1", "key2", "*"));
            assertEquals(1, nodes.size());
            nodes = pap.query().graph().search(OA, toProperties("key1", "value1", "key2", "no_value"));
            assertEquals(0, nodes.size());
            nodes = pap.query().graph().search(ANY, NO_PROPERTIES);
            assertEquals(12, nodes.size());
        }
    }

    @Nested
    class GetPolicyModificationClasses {
        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createPolicyClass("pc2", new HashMap<>());
            pap.modify().graph().createPolicyClass("pc3", new HashMap<>());

            assertTrue(pap.query().graph().getPolicyClasses().containsAll(Arrays.asList("pc1", "pc2", "pc3")));
        }
    }

    @Nested
    class DeleteNodeTest {

        @Test
        void testNodeDoesNotExistDoesNotThrowException() {
            assertDoesNotThrow(() -> pap.modify().graph().deleteNode("pc1"));
        }

        @Test
        void testNodeHasChildrenException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            assertThrows(NodeHasChildrenException.class,
                    () -> pap.modify().graph().deleteNode("pc1"));
        }

        @Test
        void DeleteNodeWithProhibitionsAndObligations() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua2"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().prohibitions().create("pro1", ProhibitionSubject.userAttribute("ua1"),
                    new AccessRightSet(), true, new ContainerCondition("oa1", true));

            assertThrows(NodeReferencedInProhibitionException.class,
                    () -> pap.modify().graph().deleteNode("ua1"));
            assertThrows(NodeReferencedInProhibitionException.class,
                    () -> pap.modify().graph().deleteNode("oa1"));

            pap.modify().prohibitions().delete("pro1");
            pap.modify().obligations().create(new UserContext("u1"), "oblLabel",
                    new Rule(
                            "rule1",
                            new EventPattern(
                                    pAscendantOf("subject", "ua1"),
                                    pEquals("op", new StringValue("event1"))
                            ),
                            new Response("evtCtx", List.of())
                    ),
                    new Rule(
                            "rule1",
                            new EventPattern(
                                    pAscendantOf("subject", "ua1"),
                                    pEquals("op", new StringValue("event1"))
                            ),
                            new Response("evtCtx", List.of())
                    )
            );

            assertThrows(NodeReferencedInObligationException.class,
                    () -> pap.modify().graph().deleteNode("ua1"));
        }

        @Test
        void testSuccessPolicyClass() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().deleteNode("pc1");
            assertFalse(pap.query().graph().nodeExists("pc1"));
            assertFalse(pap.query().graph().nodeExists(AdminPolicy.policyClassTargetName("pc1")));
        }

        @Test
        void testSuccessObjectAttribute() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            pap.modify().graph().deleteNode("oa1");

            assertFalse(pap.query().graph().nodeExists("oa1"));
        }
    }

    @Nested
    class AssignTest {

        @Test
        void testChildNodeDoesNotExistException() {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().assign("oa1", "pc1"));
        }

        @Test
        void testParentNodeDoesNotExistException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().assign("oa1", "oa2"));
        }

        @Test
        void testAssignmentExistsDoesNothing() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            assertDoesNotThrow(() -> pap.modify().graph().assign("oa1", "pc1"));
        }

        @Test
        void testInvalidAssignmentException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

            assertThrows(InvalidAssignmentException.class,
                    () -> pap.modify().graph().assign("ua1", "oa1"));
        }

        @Test
        void testAssignmentCausesLoopException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("oa1"));
            pap.modify().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("oa2"));

            assertThrows(AssignmentCausesLoopException.class, () ->
                    pap.modify().graph().assign("oa1", "oa2"));
            assertThrows(AssignmentCausesLoopException.class, () ->
                    pap.modify().graph().assign("oa1", "oa1"));
            assertThrows(AssignmentCausesLoopException.class, () ->
                    pap.modify().graph().assign("oa1", "oa3"));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().assign("oa2", "oa1");
            assertTrue(pap.query().graph().getParents("oa2").contains("oa1"));
            assertTrue(pap.query().graph().getChildren("oa1").contains("oa2"));
        }
    }

    @Nested
    class DeassignTest {

        @Test
        void testChildNodeDoesNotExistException() {
            assertThrows(NodeDoesNotExistException.class, () ->
                    pap.modify().graph().deassign("oa1", "pc1"));
        }

        @Test
        void testParentNodeDoesNotExistException() throws PMException{
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            assertThrows(NodeDoesNotExistException.class, () ->
                    pap.modify().graph().deassign("oa1", "oa2"));
        }

        @Test
        void AssignmentDoesNotExistDoesNothing() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().deassign("oa1", "oa2");
        }

        @Test
        void testDisconnectedNode() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            assertThrows(DisconnectedNodeException.class,
                    () -> pap.modify().graph().deassign("oa1", "pc1"));
        }

        @Test
        void Success() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createPolicyClass("pc2", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1", "pc2"));
            pap.modify().graph().deassign("oa1", "pc1");
            assertEquals(List.of("pc2"), pap.query().graph().getParents("oa1"));
            assertFalse(pap.query().graph().getParents("oa1").contains("pc1"));
            assertFalse(pap.query().graph().getChildren("pc1").contains("oa1"));
        }

    }

    @Nested
    class GetChildrenTest {

        @Test
        void NodeDoesNotExist() {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.query().graph().getChildren("oa1"));
        }

        @Test
        void Success() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));


            assertTrue(pap.query().graph().getChildren("pc1").containsAll(List.of("oa1", "oa2", "oa3")));
        }
    }

    @Nested
    class GetParentsTest {

        @Test
        void NodeDoesNotExist() {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.query().graph().getParents("oa1"));
        }

        @Test
        void Success() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObject("o1", new HashMap<>(), List.of("oa1"));
            pap.modify().graph().assign("o1", "oa2");
            pap.modify().graph().assign("o1", "oa3");

            assertTrue(pap.query().graph().getParents("o1").containsAll(List.of("oa1", "oa2", "oa3")));
        }
    }

    @Nested
    class AssociateTest {

        @Test
        void testUANodeDoesNotExistException() {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().associate("ua1", "oa1", new AccessRightSet()));
        }

        @Test
        void testTargetNodeDoesNotExistException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.modify().graph().associate("ua1", "oa1", new AccessRightSet()));
        }

        @Test
        void testAssignmentExistsDoesNotThrowException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("ua1"));
            assertDoesNotThrow(() -> pap.modify().graph().associate("ua2", "ua1", new AccessRightSet()));
        }

        @Test
        void testUnknownAccessRightException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            assertThrows(UnknownAccessRightException.class,
                    () -> pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read")));
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read"));
            assertThrows(UnknownAccessRightException.class,
                    () -> pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("write")));
            assertDoesNotThrow(() -> pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read")));
            assertDoesNotThrow(() -> pap.modify().graph().associate("ua1", "oa1", new AccessRightSet(ALL_ACCESS_RIGHTS)));
            assertDoesNotThrow(() -> pap.modify().graph().associate("ua1", "oa1", new AccessRightSet(ALL_RESOURCE_ACCESS_RIGHTS)));
            assertDoesNotThrow(() -> pap.modify().graph().associate("ua1", "oa1", new AccessRightSet(ALL_ADMIN_ACCESS_RIGHTS)));
        }

        @Test
        void testInvalidAssociationException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("ua1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));

            assertThrows(
                    InvalidAssociationException.class,
                    () -> pap.modify().graph().associate("ua2", "pc1", new AccessRightSet()));
            assertThrows(InvalidAssociationException.class,
                    () -> pap.modify().graph().associate("oa1", "oa2", new AccessRightSet()));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
            pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));

            assertEquals(
                    new Association("ua1", "oa1", new AccessRightSet("read")),
                    pap.query().graph().getAssociationsWithSource("ua1").get(0)
            );
            assertEquals(
                    new Association("ua1", "oa1", new AccessRightSet("read")),
                    pap.query().graph().getAssociationsWithTarget("oa1").get(0)
            );
        }

        @Test
        void testOverwriteSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
            pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));

            List<Association> assocs = pap.query().graph().getAssociationsWithSource("ua1");
            Association assoc = assocs.get(0);
            assertEquals("ua1", assoc.getSource());
            assertEquals("oa1", assoc.getTarget());
            assertEquals(new AccessRightSet("read"), assoc.getAccessRightSet());

            pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read", "write"));

            assocs = pap.query().graph().getAssociationsWithSource("ua1");
            assoc = assocs.get(0);
            assertEquals("ua1", assoc.getSource());
            assertEquals("oa1", assoc.getTarget());
            assertEquals(new AccessRightSet("read", "write"), assoc.getAccessRightSet());
        }
    }

    @Nested
    class DissociateTest {

        @Test
        void testUANodeDoesNotExistException() {
            assertThrows(NodeDoesNotExistException.class, () -> pap.modify().graph().dissociate("ua1", "oa1"));
        }

        @Test
        void testTargetNodeDoesNotExistException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

            assertThrows(NodeDoesNotExistException.class, () -> pap.modify().graph().dissociate("ua1", "oa2"));
        }

        @Test
        void testAssociationDoesNotExistDoesNotThrowException() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

            assertDoesNotThrow(() -> pap.modify().graph().dissociate("ua1", "oa1"));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().associate("ua1", "oa1", new AccessRightSet());

            pap.modify().graph().dissociate("ua1", "oa1");

            assertTrue(pap.query().graph().getAssociationsWithSource("ua1").isEmpty());
            assertTrue(pap.query().graph().getAssociationsWithTarget("oa1").isEmpty());
        }
    }

    @Nested
    class GetAssociationsWithSourceTest {

        @Test
        void testNodeDoesNotExistException() {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.query().graph().getAssociationsWithSource("ua1"));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));
            pap.modify().graph().associate("ua1", "oa2", new AccessRightSet("read", "write"));

            List<Association> assocs = pap.query().graph().getAssociationsWithSource("ua1");

            assertEquals(2, assocs.size());

            for (Association assoc : assocs) {
                checkAssociation(assoc);
            }
        }

        private void checkAssociation(Association association) {
            if (association.getTarget().equals("oa1")) {
                assertEquals(new AccessRightSet("read"), association.getAccessRightSet());
            } else if (association.getTarget().equals("oa2")) {
                assertEquals(new AccessRightSet("read", "write"), association.getAccessRightSet());
            }
        }
    }

    @Nested
    class GetAssociationsWithTargetTest {

        @Test
        void testNodeDoesNotExistException() {
            assertThrows(NodeDoesNotExistException.class,
                    () -> pap.query().graph().getAssociationsWithTarget("oa1"));
        }

        @Test
        void Success() throws PMException {
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));
            pap.modify().graph().associate("ua2", "oa1", new AccessRightSet("read", "write"));

            List<Association> assocs = pap.query().graph().getAssociationsWithTarget("oa1");

            assertEquals(2, assocs.size());

            for (Association assoc : assocs) {
                checkAssociation(assoc);
            }
        }

        private void checkAssociation(Association association) {
            if (association.getSource().equals("ua1")) {
                assertEquals(new AccessRightSet("read"), association.getAccessRightSet());
            } else if (association.getSource().equals("ua2")) {
                assertEquals(new AccessRightSet("read", "write"), association.getAccessRightSet());
            }
        }
    }
}