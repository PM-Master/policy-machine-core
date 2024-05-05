package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.serialization.json.JSONDeserializer;
import gov.nist.csd.pm.common.serialization.json.JSONSerializer;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.pml.expression.*;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.statement.CreateNonPCStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.util.PolicyEquals;
import gov.nist.csd.pm.util.SamplePolicy;
import gov.nist.csd.pm.common.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.common.serialization.pml.PMLSerializer;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.graph.nodes.Node;
import gov.nist.csd.pm.common.graph.nodes.Properties;
import gov.nist.csd.pm.common.graph.relationships.Association;
import gov.nist.csd.pm.common.graph.relationships.InvalidAssignmentException;
import gov.nist.csd.pm.common.graph.relationships.InvalidAssociationException;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static gov.nist.csd.pm.pdp.AdminAccessRights.*;
import static gov.nist.csd.pm.common.graph.nodes.NodeType.*;
import static gov.nist.csd.pm.common.graph.nodes.Properties.NO_PROPERTIES;
import static gov.nist.csd.pm.common.graph.nodes.Properties.toProperties;
import static gov.nist.csd.pm.util.PolicyEquals.assertPolicyEquals;
import static org.junit.jupiter.api.Assertions.*;

public abstract class PAPTest {

    PAP pap;

    public abstract PAP getPAP() throws PMException;

    @BeforeEach
    void setup() throws PMException {
        pap = getPAP();
    }

    @Test
    void testTx() throws PMException {
        pap.beginTx();
        pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
        pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
        pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of());
        pap.policy().graph().associate("ua1", "oa1", new AccessRightSet());
        pap.commit();

        assertTrue(pap.policy().graph().nodeExists("pc1"));
        assertTrue(pap.policy().graph().nodeExists("oa1"));
        assertTrue(pap.policy().graph().nodeExists("ua1"));
        assertTrue(pap.policy().graph().getAssociationsWithSource("ua1").get(0).equals(new Association("ua1", "oa1", new AccessRightSet())));

        pap.beginTx();
        pap.policy().graph().deleteNode("ua1");
        pap.rollback();
        assertTrue(pap.policy().graph().nodeExists("ua1"));
    }

    @Nested
    class Serialization {

        @Test
        void testErrorDuringDeserializationCausesRollback() throws PMException {
            String pml = """
                    create pc "pc1"
                    create ua "ua1" assign to ["pc2"]
                    """;

            assertThrows(PMException.class, () -> pap.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer()));
            assertFalse(pap.policy().graph().nodeExists("pc1"));
            assertFalse(pap.policy().graph().nodeExists("ua1"));
        }

        private static final String input = """
            const testConst = "hello world"
            function testFunc() {
                create pc "pc1"
            }
            
            set resource access rights ["read", "write", "execute"]
            create policy class "pc1"
            set properties of "pc1" to {"k":"v"}
            create oa "oa1" assign to ["pc1"]
            set properties of "oa1" to {"k1":"v1", "k2":"v2"}
            create ua "ua1" assign to ["pc1"]
            create u "u1" assign to ["ua1"]
            associate "ua1" and "oa1" with ["read", "write"]
            create prohibition "p1" deny user attribute "ua1" access rights ["read"] on union of ["oa1"]
            create obligation "obl1" {
                create rule "rule1"
                when any user
                performs ["event1", "event2"]
                do(evtCtx) {
                    event := evtCtx["event"]
                    if equals(event, "event1") {
                        create policy class "e1"
                    } else if equals(event, "event2") {
                        create policy class "e2"
                    }
                }
            }
            """;

        @Test
        void testSuccess() throws PMException {
            UserContext userContext = new UserContext("u1");
            pap.policy().deserialize(userContext, input, new PMLDeserializer());

            String pml = pap.policy().serialize(new PMLSerializer());
            MemoryPolicyStore policyStore = new MemoryPolicyStore();
            MemoryPolicyReviewer reviewer = new MemoryPolicyReviewer(policyStore);
            PAP pmlPAP = new PAP(policyStore, reviewer);
            pmlPAP.policy().deserialize(userContext, pml, new PMLDeserializer());

            String json = pmlPAP.policy().serialize(new JSONSerializer());
            MemoryPolicyStore jsonPS = new MemoryPolicyStore();
            MemoryPolicyReviewer jsonRev = new MemoryPolicyReviewer(policyStore);
            PAP jsonPAP = new PAP(jsonPS, jsonRev);
            jsonPAP.policy().deserialize(userContext, json, new JSONDeserializer());

            assertPolicyEquals(pap.policyStore, pmlPAP.policyStore);
            assertPolicyEquals(pap.policyStore, jsonPAP.policyStore);

            assertThrows(PMException.class, () -> {
                pap.policy().deserialize(new UserContext("unknown user"), input, new PMLDeserializer());
            });
        }
        @Test
        void testJSONAndPMLCreateEqualPolicy() throws PMException {
            UserContext userContext = new UserContext("u1");
            pap.policy().deserialize(userContext, input, new PMLDeserializer());
            String pml = pap.policy().serialize(new PMLSerializer());
            String json = pap.policy().serialize(new JSONSerializer());

            pap.policyStore.reset();
            PAP pap1 = new PAP(pap.policyStore, pap.policyReview);
            pap1.policy().deserialize(userContext, pml, new PMLDeserializer());

            pap.policyStore.reset();
            PAP pap2 = new PAP(pap.policyStore, pap.policyReview);
            pap2.policy().deserialize(userContext, json, new JSONDeserializer());

            PolicyEquals.assertPolicyEquals(pap1.policyStore, pap2.policyStore);
        }

        @Test
        void testAssignPolicyClassTargetToAnotherPolicyClass() throws PMException {
            UserContext userContext = new UserContext("u1");
            pap.policy().deserialize(userContext, input, new PMLDeserializer());

            pap.policy().graph().createObjectAttribute("test-oa", new HashMap<>(), List.of("pc1"));
            pap.policy().graph().assign(AdminPolicy.policyClassTargetName("pc1"), "test-oa");
            String pml = pap.policy().serialize(new PMLSerializer());

            MemoryPolicyStore ps = new MemoryPolicyStore();
            MemoryPolicyReviewer rev = new MemoryPolicyReviewer(ps);
            PAP pap1 = new PAP(ps, rev);
            pap1.policy().deserialize(userContext, pml, new PMLDeserializer());

            PolicyEquals.assertPolicyEquals(pap.policyStore, pap1.policyStore);
        }
    }

    @Test
    void testExecutePML() throws PMException {
        try {
            SamplePolicy.loadSamplePolicyFromPML(pap);

            FunctionDefinitionStatement functionDefinitionStatement = new FunctionDefinitionStatement.Builder("testfunc")
                    .returns(Type.voidType())
                    .args()
                    .executor((ctx, policy) -> {
                        policy.graph().createPolicyClass("pc3", new HashMap<>());
                        return new VoidValue();
                    })
                    .build();

            pap.executePML(new UserContext("u1"), "create ua \"ua3\" assign to [\"pc2\"]\ntestfunc()", functionDefinitionStatement);
            assertTrue(pap.policy().graph().nodeExists("ua3"));
            assertTrue(pap.policy().graph().nodeExists("pc3"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAdminPolicyCreatedInConstructor() throws PMException {
        testAdminPolicy(pap, 1);
    }

    @Test
    void testResetInitializesAdminPolicy() throws PMException {
        pap.policy().reset();

        testAdminPolicy(pap, 1);
    }

    public static void testAdminPolicy(PAP pap, int numExpectedPolicyClasses) throws PMException {
        assertTrue(pap.policy().graph().nodeExists(AdminPolicyNode.ADMIN_POLICY.nodeName()));
        List<String> children = pap.policy().graph().getChildren(AdminPolicyNode.ADMIN_POLICY.nodeName());
        assertEquals(5, children.size());
        assertTrue(children.containsAll(List.of(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName(),
                                                AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName(), AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), AdminPolicyNode.OBLIGATIONS_TARGET.nodeName())));

        assertTrue(pap.policy().graph().nodeExists(AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName()));
        List<String> parents = pap.policy().graph().getParents(AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName()));

        assertTrue(pap.policy().graph().nodeExists(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName()));
        children = pap.policy().graph().getChildren(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName());
        assertEquals(numExpectedPolicyClasses, children.size());
        assertTrue(children.contains(AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName()));

        parents = pap.policy().graph().getParents(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));

        assertTrue(pap.policy().graph().nodeExists(AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName()));
        parents = pap.policy().graph().getParents(AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));

        assertTrue(pap.policy().graph().nodeExists(AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName()));
        parents = pap.policy().graph().getParents(AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));

        assertTrue(pap.policy().graph().nodeExists(AdminPolicyNode.PROHIBITIONS_TARGET.nodeName()));
        parents = pap.policy().graph().getParents(AdminPolicyNode.PROHIBITIONS_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));

        assertTrue(pap.policy().graph().nodeExists(AdminPolicyNode.OBLIGATIONS_TARGET.nodeName()));
        parents = pap.policy().graph().getParents(AdminPolicyNode.OBLIGATIONS_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));
    }


    @Nested
    class GraphTests {

        @Nested
        class SetResourceAccessRights {

            @Test
            void testAdminAccessRightExistsException() {
                assertThrows(AdminAccessRightExistsException.class, () ->
                        pap.policy().graph().setResourceAccessRights(new AccessRightSet(CREATE_POLICY_CLASS)));
            }

            @Test
            void testSuccess() throws PMException {
                AccessRightSet arset = new AccessRightSet("read", "write");
                pap.policy().graph().setResourceAccessRights(arset);
                assertEquals(arset, pap.policy().graph().getResourceAccessRights());
            }

        }

        @Nested
        class GetResourceAccessRights {
            @Test
            void testGetResourceAccessRights() throws PMException {
                AccessRightSet arset = new AccessRightSet("read", "write");
                pap.policy().graph().setResourceAccessRights(arset);
                assertEquals(arset, pap.policy().graph().getResourceAccessRights());
            }
        }

        @Nested
        class CreatePolicyClassTest {
            @Test
            void testNodeNameExistsException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                assertDoesNotThrow(() -> pap.policy().graph().createPolicyClass("pc2", new HashMap<>()));
                assertThrows(NodeNameExistsException.class, () -> pap.policy().graph().createPolicyClass("pc1", new HashMap<>()));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                String rep = AdminPolicy.policyClassTargetName("pc1");
                assertTrue(pap.policy().graph().nodeExists("pc1"));
                assertTrue(pap.policy().graph().nodeExists(rep));
                assertTrue(pap.policy().graph().getParents(rep).contains(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName()));
                assertTrue(pap.policy().graph().getChildren(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName()).contains(rep));
            }
        }

        @Nested
        class CreateObjectAttribute {

            @Test
            void testNodeNameExistsException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                assertThrows(NodeNameExistsException.class,
                             () -> pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1")));
            }

            @Test
            void testNodeDoesNotExistException() throws PMException {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1")));

                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1", "pc2")));
            }

            @Test
            void testInvalidAssignmentException()
                    throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

                assertThrows(InvalidAssignmentException.class,
                             () -> pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("ua1")));
            }

            @Test
            void testAssignmentCausesLoopException()
                    throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("oa1"));

                assertThrows(AssignmentCausesLoopException.class,
                             () -> pap.policy().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("oa3")));
                assertThrows(AssignmentCausesLoopException.class,
                             () -> pap.policy().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("oa2", "oa3")));
            }

            @Test
            void Success() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", toProperties("k", "v"), List.of("oa1"));

                assertTrue(pap.policy().graph().nodeExists("oa1"));
                assertTrue(pap.policy().graph().nodeExists("oa2"));
                assertEquals("v", pap.policy().graph().getNode("oa2").getProperties().get("k"));

                assertTrue(pap.policy().graph().getChildren("pc1").contains("oa1"));
                assertTrue(pap.policy().graph().getChildren("oa1").contains("oa2"));

                assertTrue(pap.policy().graph().getParents("oa1").contains("pc1"));
                assertTrue(pap.policy().graph().getParents("oa2").contains("oa1"));
            }

            @Test
            void testNoParents() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                assertThrows(DisconnectedNodeException.class, () -> pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of()));
            }
        }

        @Nested
        class CreateUserAttributeTest {

            @Test
            void testNodeNameExistsException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                assertThrows(NodeNameExistsException.class,
                             () -> pap.policy().graph().createObjectAttribute("ua1", new HashMap<>(), List.of("pc1")));
            }

            @Test
            void testNodeDoesNotExistException() throws PMException {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1")));

                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1", "pc2")));
            }

            @Test
            void testInvalidAssignmentException()
                    throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                assertThrows(InvalidAssignmentException.class,
                             () -> pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("oa1")));
            }

            @Test
            void testAssignmentCausesLoopException()
                    throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua2", new HashMap<>(), List.of("ua1"));

                assertThrows(AssignmentCausesLoopException.class,
                             () -> pap.policy().graph().createUserAttribute("ua3", new HashMap<>(), List.of("ua3")));
                assertThrows(AssignmentCausesLoopException.class,
                             () -> pap.policy().graph().createUserAttribute("ua3", new HashMap<>(), List.of("ua2", "ua3")));
            }

            @Test
            void Success() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of());
                pap.policy().graph().createUserAttribute("ua2", toProperties("k", "v"), List.of("ua1"));

                assertTrue(pap.policy().graph().nodeExists("ua1"));
                assertTrue(pap.policy().graph().nodeExists("ua2"));
                assertEquals("v", pap.policy().graph().getNode("ua2").getProperties().get("k"));

                assertTrue(pap.policy().graph().getChildren("pc1").isEmpty());
                assertTrue(pap.policy().graph().getParents("ua1").isEmpty());

                assertTrue(pap.policy().graph().getChildren("ua1").contains("ua2"));
                assertTrue(pap.policy().graph().getParents("ua2").contains("ua1"));
            }
        }

        @Nested
        class CreateObjectTest {

            @Test
            void testNodeNameExistsException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObject("o1", new HashMap<>(), List.of("oa1"));
                assertThrows(NodeNameExistsException.class,
                             () -> pap.policy().graph().createObject("o1", new HashMap<>(), List.of("oa1")));
            }

            @Test
            void testNodeDoesNotExistException() throws PMException {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().createObject("o1", new HashMap<>(), List.of("oa1")));

                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().createObjectAttribute("o1", new HashMap<>(), List.of("oa1", "oa2")));
            }

            @Test
            void testInvalidAssignmentException()
                    throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

                assertThrows(InvalidAssignmentException.class,
                             () -> pap.policy().graph().createObjectAttribute("o1", new HashMap<>(), List.of("ua1")));
            }

            @Test
            void testAssignmentCausesLoopException()
                    throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                assertThrows(AssignmentCausesLoopException.class,
                             () -> pap.policy().graph().createObject("o1", new HashMap<>(), List.of("o1")));
                assertThrows(AssignmentCausesLoopException.class,
                             () -> pap.policy().graph().createObject("o1", new HashMap<>(), List.of("oa1", "o1")));
            }

            @Test
            void Success() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                pap.policy().graph().createObject("o1", toProperties("k", "v"), List.of("oa1"));

                assertTrue(pap.policy().graph().nodeExists("o1"));
                assertEquals("v", pap.policy().graph().getNode("o1").getProperties().get("k"));

                assertTrue(pap.policy().graph().getChildren("oa1").contains("o1"));
                assertEquals( List.of("oa1"), pap.policy().graph().getParents("o1"));
                assertTrue(pap.policy().graph().getChildren("oa1").contains("o1"));
            }
        }

        @Nested
        class CreateUserTest {

            @Test
            void testNodeNameExistsException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
                assertThrows(NodeNameExistsException.class,
                             () -> pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1")));
            }

            @Test
            void testNodeDoesNotExistException() throws PMException {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1")));

                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1", "ua2")));
            }

            @Test
            void testInvalidAssignmentException()
                    throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                assertThrows(InvalidAssignmentException.class,
                             () -> pap.policy().graph().createUser("u1", new HashMap<>(), List.of("oa1")));
            }

            @Test
            void testAssignmentCausesLoopException()
                    throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

                assertThrows(AssignmentCausesLoopException.class,
                             () -> pap.policy().graph().createUser("u1", new HashMap<>(), List.of("u1")));
                assertThrows(AssignmentCausesLoopException.class,
                             () -> pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1", "u1")));
            }

            @Test
            void Success() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

                pap.policy().graph().createUser("u1", toProperties("k", "v"), List.of("ua1"));

                assertTrue(pap.policy().graph().nodeExists("u1"));
                assertEquals("v", pap.policy().graph().getNode("u1").getProperties().get("k"));

                assertTrue(pap.policy().graph().getChildren("ua1").contains("u1"));
                assertEquals( List.of("ua1"), pap.policy().graph().getParents("u1"));
                assertTrue(pap.policy().graph().getChildren("ua1").contains("u1"));
            }
        }

        @Nested
        class SetNodePropertiesTest {

            @Test
            void testNodeDoesNotExistException() {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().setNodeProperties("oa1", NO_PROPERTIES));
            }

            @Test
            void testSuccessEmptyProperties() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().setNodeProperties("pc1", NO_PROPERTIES);

                assertTrue(pap.policy().graph().getNode("pc1").getProperties().isEmpty());
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().setNodeProperties("pc1", toProperties("k", "v"));

                assertEquals("v", pap.policy().graph().getNode("pc1").getProperties().get("k"));
            }
        }

        @Nested
        class NodeExists {
            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                assertTrue(pap.policy().graph().nodeExists("pc1"));
                assertTrue(pap.policy().graph().nodeExists("ua1"));
                assertFalse(pap.policy().graph().nodeExists("pc2"));
            }
        }

        @Nested
        class GetNodeTest {

            @Test
            void testNodeDoesNotExistException() {
                assertThrows(NodeDoesNotExistException.class, () -> pap.policy().graph().getNode("pc1"));
            }

            @Test
            void testSuccessPolicyClass() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", Properties.toProperties("k", "v"));

                Node pc1 = pap.policy().graph().getNode("pc1");

                assertEquals("pc1", pc1.getName());
                assertEquals(PC, pc1.getType());
                assertEquals("v", pc1.getProperties().get("k"));
            }

            @Test
            void testSuccessObjectAttribute() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", Properties.toProperties("k", "v"), List.of("pc1"));

                Node oa1 = pap.policy().graph().getNode("oa1");

                assertEquals("oa1", oa1.getName());
                assertEquals(OA, oa1.getType());
                assertEquals("v", oa1.getProperties().get("k"));
            }
        }

        @Nested
        class Search {
            @Test
            void testSearch() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", toProperties("namespace", "test"), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", toProperties("key1", "value1"), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa3", toProperties("key1", "value1", "key2", "value2"), List.of("pc1"));

                List<String> nodes = pap.policy().graph().search(OA, NO_PROPERTIES);
                assertEquals(10, nodes.size());

                nodes = pap.policy().graph().search(ANY, toProperties("key1", "value1"));
                assertEquals(2, nodes.size());

                nodes = pap.policy().graph().search(ANY, toProperties("namespace", "test"));
                assertEquals(1, nodes.size());

                nodes = pap.policy().graph().search(OA, toProperties("namespace", "test"));
                assertEquals(1, nodes.size());
                nodes = pap.policy().graph().search(OA, toProperties("key1", "value1"));
                assertEquals(2, nodes.size());
                nodes = pap.policy().graph().search(OA, toProperties("key1", "*"));
                assertEquals(2, nodes.size());
                nodes = pap.policy().graph().search(OA, toProperties("key1", "value1", "key2", "value2"));
                assertEquals(1, nodes.size());
                nodes = pap.policy().graph().search(OA, toProperties("key1", "value1", "key2", "*"));
                assertEquals(1, nodes.size());
                nodes = pap.policy().graph().search(OA, toProperties("key1", "value1", "key2", "no_value"));
                assertEquals(0, nodes.size());
                nodes = pap.policy().graph().search(ANY, NO_PROPERTIES);
                assertEquals(12, nodes.size());
            }
        }

        @Nested
        class GetPolicyClasses {
            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createPolicyClass("pc2", new HashMap<>());
                pap.policy().graph().createPolicyClass("pc3", new HashMap<>());

                assertTrue(pap.policy().graph().getPolicyClasses().containsAll(Arrays.asList("pc1", "pc2", "pc3")));
            }
        }

        @Nested
        class DeleteNodeTest {

            @Test
            void testNodeDoesNotExistDoesNotThrowException() {
                assertDoesNotThrow(() -> pap.policy().graph().deleteNode("pc1"));
            }

            @Test
            void testNodeHasChildrenException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                assertThrows(NodeHasChildrenException.class,
                             () -> pap.policy().graph().deleteNode("pc1"));
            }

            @Test
            void DeleteNodeWithProhibitionsAndObligations() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua2"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("ua1"),
                                          new AccessRightSet(), true, new ContainerCondition("oa1", true));

                assertThrows(NodeReferencedInProhibitionException.class,
                             () -> pap.policy().graph().deleteNode("ua1"));
                assertThrows(NodeReferencedInProhibitionException.class,
                             () -> pap.policy().graph().deleteNode("oa1"));

                pap.policy().prohibitions().delete("pro1");
                fail("nyi");
                /* pap.policy().obligations().create(new UserContext("u1"), "oblLabel",
                                         new Rule(
                                                 "rule1",
                                                 new EventPattern(
                                                         new UserAttributesSubject("ua1"),
                                                         new Performs("event1")
                                                 ),
                                                 new Response("evtCtx", List.of())
                                         ),
                                         new Rule(
                                                 "rule1",
                                                 new EventPattern(
                                                         new UsersSubject("ua1"),
                                                         new Performs("event1")
                                                 ),
                                                 new Response("evtCtx", List.of())
                                         )
                );*/

                assertThrows(NodeReferencedInObligationException.class,
                             () -> pap.policy().graph().deleteNode("ua1"));
            }

            @Test
            void testSuccessPolicyClass() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().deleteNode("pc1");
                assertFalse(pap.policy().graph().nodeExists("pc1"));
                assertFalse(pap.policy().graph().nodeExists(AdminPolicy.policyClassTargetName("pc1")));
            }

            @Test
            void testSuccessObjectAttribute() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                pap.policy().graph().deleteNode("oa1");

                assertFalse(pap.policy().graph().nodeExists("oa1"));
            }
        }

        @Nested
        class AssignTest {

            @Test
            void testChildNodeDoesNotExistException() {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().assign("oa1", "pc1"));
            }

            @Test
            void testParentNodeDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().assign("oa1", "oa2"));
            }

            @Test
            void testAssignmentExistsDoesNothing() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                assertDoesNotThrow(() -> pap.policy().graph().assign("oa1", "pc1"));
            }

            @Test
            void testInvalidAssignmentException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

                assertThrows(InvalidAssignmentException.class,
                             () -> pap.policy().graph().assign("ua1", "oa1"));
            }

            @Test
            void testAssignmentCausesLoopException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("oa1"));
                pap.policy().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("oa2"));

                assertThrows(AssignmentCausesLoopException.class, () ->
                        pap.policy().graph().assign("oa1", "oa2"));
                assertThrows(AssignmentCausesLoopException.class, () ->
                        pap.policy().graph().assign("oa1", "oa1"));
                assertThrows(AssignmentCausesLoopException.class, () ->
                        pap.policy().graph().assign("oa1", "oa3"));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().assign("oa2", "oa1");
                assertTrue(pap.policy().graph().getParents("oa2").contains("oa1"));
                assertTrue(pap.policy().graph().getChildren("oa1").contains("oa2"));
            }
        }

        @Nested
        class DeassignTest {

            @Test
            void testChildNodeDoesNotExistException() {
                assertThrows(NodeDoesNotExistException.class, () ->
                        pap.policy().graph().deassign("oa1", "pc1"));
            }

            @Test
            void testParentNodeDoesNotExistException() throws PMException{
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                assertThrows(NodeDoesNotExistException.class, () ->
                        pap.policy().graph().deassign("oa1", "oa2"));
            }

            @Test
            void AssignmentDoesNotExistDoesNothing() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().deassign("oa1", "oa2");
            }

            @Test
            void testDisconnectedNode() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                assertThrows(DisconnectedNodeException.class,
                             () -> pap.policy().graph().deassign("oa1", "pc1"));
            }

            @Test
            void Success() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createPolicyClass("pc2", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1", "pc2"));
                pap.policy().graph().deassign("oa1", "pc1");
                assertEquals(List.of("pc2"), pap.policy().graph().getParents("oa1"));
                assertFalse(pap.policy().graph().getParents("oa1").contains("pc1"));
                assertFalse(pap.policy().graph().getChildren("pc1").contains("oa1"));
            }

        }

        @Nested
        class GetChildrenTest {

            @Test
            void NodeDoesNotExist() {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().getChildren("oa1"));
            }

            @Test
            void Success() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));


                assertTrue(pap.policy().graph().getChildren("pc1").containsAll(List.of("oa1", "oa2", "oa3")));
            }
        }

        @Nested
        class GetParentsTest {

            @Test
            void NodeDoesNotExist() {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().getParents("oa1"));
            }

            @Test
            void Success() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObject("o1", new HashMap<>(), List.of("oa1"));
                pap.policy().graph().assign("o1", "oa2");
                pap.policy().graph().assign("o1", "oa3");

                assertTrue(pap.policy().graph().getParents("o1").containsAll(List.of("oa1", "oa2", "oa3")));
            }
        }

        @Nested
        class AssociateTest {

            @Test
            void testUANodeDoesNotExistException() {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().associate("ua1", "oa1", new AccessRightSet()));
            }

            @Test
            void testTargetNodeDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().associate("ua1", "oa1", new AccessRightSet()));
            }

            @Test
            void testAssignmentExistsDoesNotThrowException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua2", new HashMap<>(), List.of("ua1"));
                assertDoesNotThrow(() -> pap.policy().graph().associate("ua2", "ua1", new AccessRightSet()));
            }

            @Test
            void testUnknownAccessRightException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                assertThrows(UnknownAccessRightException.class,
                             () -> pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read")));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read"));
                assertThrows(UnknownAccessRightException.class,
                             () -> pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("write")));
                assertDoesNotThrow(() -> pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read")));
                assertDoesNotThrow(() -> pap.policy().graph().associate("ua1", "oa1", new AccessRightSet(ALL_ACCESS_RIGHTS)));
                assertDoesNotThrow(() -> pap.policy().graph().associate("ua1", "oa1", new AccessRightSet(ALL_RESOURCE_ACCESS_RIGHTS)));
                assertDoesNotThrow(() -> pap.policy().graph().associate("ua1", "oa1", new AccessRightSet(ALL_ADMIN_ACCESS_RIGHTS)));
            }

            @Test
            void testInvalidAssociationException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua2", new HashMap<>(), List.of("ua1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));

                assertThrows(InvalidAssociationException.class,
                             () -> pap.policy().graph().associate("ua2", "pc1", new AccessRightSet()));
                assertThrows(InvalidAssociationException.class,
                             () -> pap.policy().graph().associate("oa1", "oa2", new AccessRightSet()));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
                pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));

                assertEquals(
                        new Association("ua1", "oa1", new AccessRightSet("read")),
                        pap.policy().graph().getAssociationsWithSource("ua1").get(0)
                );
                assertEquals(
                        new Association("ua1", "oa1", new AccessRightSet("read")),
                        pap.policy().graph().getAssociationsWithTarget("oa1").get(0)
                );
            }

            @Test
            void testOverwriteSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
                pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));

                List<Association> assocs = pap.policy().graph().getAssociationsWithSource("ua1");
                Association assoc = assocs.get(0);
                assertEquals("ua1", assoc.getSource());
                assertEquals("oa1", assoc.getTarget());
                assertEquals(new AccessRightSet("read"), assoc.getAccessRightSet());

                pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read", "write"));

                assocs = pap.policy().graph().getAssociationsWithSource("ua1");
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
                assertThrows(NodeDoesNotExistException.class, () -> pap.policy().graph().dissociate("ua1", "oa1"));
            }

            @Test
            void testTargetNodeDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

                assertThrows(NodeDoesNotExistException.class, () -> pap.policy().graph().dissociate("ua1", "oa2"));
            }

            @Test
            void testAssociationDoesNotExistDoesNotThrowException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));

                assertDoesNotThrow(() -> pap.policy().graph().dissociate("ua1", "oa1"));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().associate("ua1", "oa1", new AccessRightSet());

                pap.policy().graph().dissociate("ua1", "oa1");

                assertTrue(pap.policy().graph().getAssociationsWithSource("ua1").isEmpty());
                assertTrue(pap.policy().graph().getAssociationsWithTarget("oa1").isEmpty());
            }
        }

        @Nested
        class GetAssociationsWithSourceTest {

            @Test
            void testNodeDoesNotExistException() {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().graph().getAssociationsWithSource("ua1"));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));
                pap.policy().graph().associate("ua1", "oa2", new AccessRightSet("read", "write"));

                List<Association> assocs = pap.policy().graph().getAssociationsWithSource("ua1");

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
                             () -> pap.policy().graph().getAssociationsWithTarget("oa1"));
            }

            @Test
            void Success() throws PMException {
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));
                pap.policy().graph().associate("ua2", "oa1", new AccessRightSet("read", "write"));

                List<Association> assocs = pap.policy().graph().getAssociationsWithTarget("oa1");

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

    @Nested
    class ProhibitionsTests {
        @Nested
        class CreateProhibitionTest {

            @Test
            void testProhibitionExistsException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));

                pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet(), false);

                assertThrows(ProhibitionExistsException.class,
                             () -> pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet(), false));
            }

            @Test
            void testProhibitionSubjectDoesNotExistException() {
                assertThrows(ProhibitionSubjectDoesNotExistException.class,
                             () -> pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet(ALL_ADMIN_ACCESS_RIGHTS), false));
            }


            @Test
            void testUnknownAccessRightException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));

                assertThrows(UnknownAccessRightException.class,
                             () -> pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), false));
            }

            @Test
            void testProhibitionContainerDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read"));
                assertThrows(ProhibitionContainerDoesNotExistException.class,
                             () -> pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), false, new ContainerCondition("oa1", true)));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

                pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", true),
                                          new ContainerCondition("oa2", false));

                Prohibition p = pap.policy().prohibitions().get("pro1");
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
                pap.policy().graph().createPolicyClass("pc", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua", new HashMap<>(), List.of("pc"));

                assertThrows(ProhibitionDoesNotExistException.class,
                             () -> pap.policy().prohibitions().update("pro1", ProhibitionSubject.userAttribute("ua"), new AccessRightSet(
                                     CREATE_POLICY_CLASS), false));
            }


            @Test
            void testUnknownAccessRightException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

                pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", true));

                assertThrows(UnknownAccessRightException.class,
                             () -> pap.policy().prohibitions().update("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("test"), false));
            }

            @Test
            void testProhibitionSubjectDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

                pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", true));

                assertThrows(ProhibitionSubjectDoesNotExistException.class,
                             () -> pap.policy().prohibitions().update("pro1", ProhibitionSubject.userAttribute("test"), new AccessRightSet("read"), false));
                assertDoesNotThrow(() -> pap.policy().prohibitions().update("pro1", ProhibitionSubject.process("subject"), new AccessRightSet("read"), false));
            }

            @Test
            void testProhibitionContainerDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

                pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", true));

                assertThrows(ProhibitionContainerDoesNotExistException.class,
                             () -> pap.policy().prohibitions().update("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), false, new ContainerCondition("oa3", true)));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("subject2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

                pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", true),
                                          new ContainerCondition("oa2", false));
                pap.policy().prohibitions().update("pro1", ProhibitionSubject.userAttribute("subject2"), new AccessRightSet("read", "write"), true,
                                          new ContainerCondition("oa1", false),
                                          new ContainerCondition("oa2", true));

                Prohibition p = pap.policy().prohibitions().get("pro1");
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
                assertDoesNotThrow(() -> pap.policy().prohibitions().delete("pro1"));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

                pap.policy().prohibitions().create("pro1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", true),
                                          new ContainerCondition("oa2", false));

                assertDoesNotThrow(() -> pap.policy().prohibitions().get("pro1"));

                pap.policy().prohibitions().delete("pro1");

                assertThrows(ProhibitionDoesNotExistException.class,
                             () -> pap.policy().prohibitions().get("pro1"));
            }
        }

        @Nested
        class GetAll {

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa4", new HashMap<>(), List.of("pc1"));

                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

                pap.policy().prohibitions().create("label1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", true),
                                          new ContainerCondition("oa2", false));
                pap.policy().prohibitions().create("label2", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa3", true),
                                          new ContainerCondition("oa4", false));

                Map<String, List<Prohibition>> prohibitions = pap.policy().prohibitions().getAll();
                assertEquals(1, prohibitions.size());
                assertEquals(2, prohibitions.get("subject").size());
                checkProhibitions(prohibitions.get("subject"));
            }

            private void checkProhibitions(List<Prohibition> prohibitions) {
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
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("subject2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa4", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

                pap.policy().prohibitions().create("label1", ProhibitionSubject.userAttribute("subject1"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", true),
                                          new ContainerCondition("oa2", false));
                pap.policy().prohibitions().create("label2", ProhibitionSubject.userAttribute("subject2"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa3", true),
                                          new ContainerCondition("oa4", false));

                List<Prohibition> pros = pap.policy().prohibitions().getWithSubject("subject1");
                assertEquals(1, pros.size());

                Prohibition p = pros.get(0);

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
                             () -> pap.policy().prohibitions().get("pro1"));

                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("subject", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createObjectAttribute("oa4", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read", "write"));

                pap.policy().prohibitions().create("label1", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", true),
                                          new ContainerCondition("oa2", false));
                pap.policy().prohibitions().create("label2", ProhibitionSubject.userAttribute("subject"), new AccessRightSet("read"), true,
                                          new ContainerCondition("oa3", true),
                                          new ContainerCondition("oa4", false));

                Prohibition p = pap.policy().prohibitions().get("label1");
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

    /*
    @Nested
    // TODO
    class ObligationsTests {

        Obligation obligation1 = new Obligation(
                new UserContext("u1"),
                "obl1",
                List.of(
                        new Rule(
                                "rule1",
                                new EventPattern(
                                        new AnyUserSubject(),
                                        new Performs("test_event")
                                ),
                                new Response("evtCtx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("test_pc"))
                                ))
                        )
                )
        );

        Obligation obligation2 = new Obligation(
                new UserContext("u1"),
                "label2")
                .addRule(
                        new Rule(
                                "rule1",
                                new EventPattern(
                                        new AnyUserSubject(),
                                        new Performs("test_event")
                                ),
                                new Response("evtCtx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("test_pc"))
                                ))
                        )
                ).addRule(
                        new Rule(
                                "rule2",
                                new EventPattern(
                                        new AnyUserSubject(),
                                        new Performs("test_event")
                                ),
                                new Response("evtCtx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("test_pc"))
                                ))
                        )
                );


        @Nested
        class CreateObligation {

            @Test
            void testObligationNameExistsException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

                assertThrows(ObligationNameExistsException.class, () -> pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new)));
            }

            @Test
            void testAuthorNodeDoestNotExistException() {
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().create(new UserContext("u1"), obligation1.getName(),
                                                            obligation1.getRules().toArray(Rule[]::new)));
            }

            @Test
            void testEventSubjectNodeDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().create(
                                     new UserContext("u1"),
                                     "obl1",
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UsersSubject("ua2"),
                                                     Performs.events("test_event"),
                                                     new AnyTarget()
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().create(
                                     new UserContext("u1"),
                                     "obl1",
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UserAttributesSubject("ua3"),
                                                     Performs.events("test_event"),
                                                     new AnyTarget()
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
            }

            @Test
            void testEventTargetNodeDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().create(
                                     new UserContext("u1"),
                                     "obl1",
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UsersSubject("u1"),
                                                     Performs.events("test_event"),
                                                     new OnTargets("oa1")
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().create(
                                     new UserContext("u1"),
                                     "obl1",
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UsersSubject("u1"),
                                                     Performs.events("test_event"),
                                                     new OnTargets("oa1")
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().create(
                                     new UserContext("u1"),
                                     "obl1",
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UsersSubject("u1"),
                                                     Performs.events("test_event"),
                                                     new AnyInUnionTarget("oa1")
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

                assertThrows(ObligationNameExistsException.class,
                             () -> pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName()));

                Obligation actual = pap.policy().obligations().get(obligation1.getName());
                assertEquals(obligation1, actual);
            }
        }

        @Nested
        class UpdateObligation {

            @Test
            void testObligationDoesNotExistException() {
                assertThrows(ObligationDoesNotExistException.class, () -> pap.policy().obligations().update(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new)));
            }

            @Test
            void testAuthorNodeDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().update(new UserContext("u2"), obligation1.getName(),
                                                            obligation1.getRules().toArray(Rule[]::new)));
            }

            @Test
            void testEventSubjectNodeDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().update(
                                     new UserContext("u1"),
                                     obligation1.getName(),
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UsersSubject("ua2"),
                                                     Performs.events("test_event"),
                                                     new AnyTarget()
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().update(
                                     new UserContext("u1"),
                                     obligation1.getName(),
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UserAttributesSubject("ua2"),
                                                     Performs.events("test_event"),
                                                     new AnyTarget()
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
            }

            @Test
            void testEventTargetNodeDoesNotExistException() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().update(
                                     new UserContext("u1"),
                                     obligation1.getName(),
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UsersSubject("u1"),
                                                     Performs.events("test_event"),
                                                     new OnTargets("oa1")
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().update(
                                     new UserContext("u1"),
                                     obligation1.getName(),
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UsersSubject("u1"),
                                                     Performs.events("test_event"),
                                                     new OnTargets("oa1")
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
                assertThrows(NodeDoesNotExistException.class,
                             () -> pap.policy().obligations().update(
                                     new UserContext("u1"),
                                     obligation1.getName(),
                                     new Rule(
                                             "rule1",
                                             new EventPattern(
                                                     new UsersSubject("u1"),
                                                     Performs.events("test_event"),
                                                     new AnyInUnionTarget("oa1")
                                             ),
                                             new Response("evtCtx", List.of())
                                     )
                             ));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                assertThrows(ObligationDoesNotExistException.class,
                             () -> pap.policy().obligations().update(new UserContext("u1"), obligation1.getName()));

                pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));

                pap.policy().obligations().update(new UserContext("u1"), obligation1.getName(),
                                         obligation2.getRules().toArray(Rule[]::new));

                Obligation expected = new Obligation(obligation1);
                expected.setRules(obligation2.getRules());

                Obligation actual = pap.policy().obligations().get(obligation1.getName());
                assertEquals(expected, actual);
            }

        }

        @Nested
        class DeleteNode {

            @Test
            void testDeleteNonExistingObligationDoesNOtThrowExcpetion() {
                assertDoesNotThrow(() -> pap.policy().obligations().delete(obligation1.getName()));
            }

            @Test
            void testDeleteObligation() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));
                pap.policy().obligations().create(obligation2.getAuthor(), obligation2.getName(), obligation2.getRules().toArray(Rule[]::new));

                pap.policy().obligations().delete(obligation1.getName());

                assertThrows(ObligationDoesNotExistException.class,
                             () -> pap.policy().obligations().get(obligation1.getName()));
            }
        }


        @Nested
        class GetAll {
            @Test
            void testGetObligations() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));
                pap.policy().obligations().create(obligation2.getAuthor(), obligation2.getName(), obligation2.getRules().toArray(Rule[]::new));

                List<Obligation> obligations = pap.policy().obligations().getAll();
                assertEquals(2, obligations.size());
                for (Obligation obligation : obligations) {
                    if (obligation.getName().equals(obligation1.getName())) {
                        assertEquals(obligation1, obligation);
                    } else {
                        assertEquals(obligation2, obligation);
                    }
                }
            }
        }


        @Nested
        class Get {

            @Test
            void testObligationDoesNotExistException() {
                assertThrows(ObligationDoesNotExistException.class,
                             () -> pap.policy().obligations().get(obligation1.getName()));
            }

            @Test
            void testGetObligation() throws PMException {
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(obligation1.getAuthor(), obligation1.getName(), obligation1.getRules().toArray(Rule[]::new));
                pap.policy().obligations().create(obligation2.getAuthor(), obligation2.getName(), obligation2.getRules().toArray(Rule[]::new));

                Obligation obligation = pap.policy().obligations().get(obligation1.getName());
                assertEquals(obligation1, obligation);
            }
        }
    }*/

    @Nested
    class UserDefinedPMLTests {

        @Nested
        class CreateFunction {

            FunctionDefinitionStatement testFunc = new FunctionDefinitionStatement.Builder("testFunc")
                    .returns(Type.string())
                    .args(
                            new FormalArgument("arg1", Type.string()),
                            new FormalArgument("arg2", Type.array(Type.string()))
                    )
                    .body(
                            new CreatePolicyStatement(new StringLiteral("pc1")),
                            new CreateNonPCStatement(
                                    new StringLiteral("ua1"),
                                    UA,
                                    new ArrayLiteral(new Expression[]{new StringLiteral("pc1")}, Type.string())
                            ),
                            new CreateNonPCStatement(
                                    new StringLiteral("oa1"),
                                    OA,
                                    new ArrayLiteral(new Expression[]{new StringLiteral("pc1")}, Type.string())
                            )
                    )
                    .build();

            @Test
            void testPMLFunctionAlreadyDefinedException() throws PMException {
                pap.policy().userDefinedPML().createFunction(testFunc);
                assertThrows(PMLFunctionAlreadyDefinedException.class, () -> pap.policy().userDefinedPML().createFunction(testFunc));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().userDefinedPML().createFunction(testFunc);
                assertTrue(pap.policy().userDefinedPML().getFunctions().containsKey(testFunc.getSignature().getFunctionName()));
                FunctionDefinitionStatement actual = pap.policy().userDefinedPML().getFunctions().get(testFunc.getSignature().getFunctionName());
                assertEquals(testFunc, actual);
            }
        }

        @Nested
        class DeleteFunction {

            @Test
            void testNonExistingFunctionDoesNotThrowException() {
                assertDoesNotThrow(() -> pap.policy().userDefinedPML().deleteFunction("func"));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().userDefinedPML().createFunction(new FunctionDefinitionStatement.Builder("testFunc").returns(Type.voidType()).build());
                assertTrue(pap.policy().userDefinedPML().getFunctions().containsKey("testFunc"));
                pap.policy().userDefinedPML().deleteFunction("testFunc");
                assertFalse(pap.policy().userDefinedPML().getFunctions().containsKey("testFunc"));
            }
        }

        @Nested
        class GetFunctions {

            @Test
            void testSuccess() throws PMException {
                FunctionDefinitionStatement testFunc1 = new FunctionDefinitionStatement.Builder("testFunc1").returns(Type.voidType()).build();
                FunctionDefinitionStatement testFunc2 = new FunctionDefinitionStatement.Builder("testFunc2").returns(Type.voidType()).build();

                pap.policy().userDefinedPML().createFunction(testFunc1);
                pap.policy().userDefinedPML().createFunction(testFunc2);

                Map<String, FunctionDefinitionStatement> functions = pap.policy().userDefinedPML().getFunctions();
                assertTrue(functions.containsKey("testFunc1"));
                FunctionDefinitionStatement actual = functions.get("testFunc1");
                assertEquals(testFunc1, actual);

                assertTrue(functions.containsKey("testFunc2"));
                actual = functions.get("testFunc2");
                assertEquals(testFunc2, actual);
            }

        }

        @Nested
        class GetFunction {

            @Test
            void testPMLFunctionNotDefinedException() {
                assertThrows(PMLFunctionNotDefinedException.class, () -> pap.policy().userDefinedPML().getFunction("func1"));
            }

            @Test
            void testSuccess() throws PMException {
                FunctionDefinitionStatement testFunc1 = new FunctionDefinitionStatement.Builder("testFunc1").returns(Type.voidType()).build();
                FunctionDefinitionStatement testFunc2 = new FunctionDefinitionStatement.Builder("testFunc2").returns(Type.voidType()).build();

                pap.policy().userDefinedPML().createFunction(testFunc1);
                pap.policy().userDefinedPML().createFunction(testFunc2);

                Map<String, FunctionDefinitionStatement> functions = pap.policy().userDefinedPML().getFunctions();
                assertTrue(functions.containsKey("testFunc1"));
                FunctionDefinitionStatement actual = functions.get("testFunc1");
                assertEquals(testFunc1, actual);

                assertTrue(functions.containsKey("testFunc2"));
                actual = functions.get("testFunc2");
                assertEquals(testFunc2, actual);
            }

        }

        @Nested
        class CreateConstant {

            @Test
            void testPMLConstantAlreadyDefinedException() throws PMException {
                pap.policy().userDefinedPML().createConstant("const1", new StringValue("test"));
                assertThrows(PMLConstantAlreadyDefinedException.class,
                             () -> pap.policy().userDefinedPML().createConstant("const1", new StringValue("test")));
            }

            @Test
            void testSuccess() throws PMException {
                StringValue expected = new StringValue("test");

                pap.policy().userDefinedPML().createConstant("const1", expected);
                assertTrue(pap.policy().userDefinedPML().getConstants().containsKey("const1"));
                Value actual = pap.policy().userDefinedPML().getConstants().get("const1");
                assertEquals(expected, actual);
            }
        }

        @Nested
        class DeleteConstant {

            @Test
            void testNonExistingConstantDoesNotThrowException() {
                assertDoesNotThrow(() -> pap.policy().userDefinedPML().deleteConstant("const1"));
            }

            @Test
            void testSuccess() throws PMException {
                pap.policy().userDefinedPML().createConstant("const1", new StringValue("test"));
                assertTrue(pap.policy().userDefinedPML().getConstants().containsKey("const1"));
                pap.policy().userDefinedPML().deleteConstant("const1");
                assertFalse(pap.policy().userDefinedPML().getConstants().containsKey("const1"));
            }
        }

        @Nested
        class GetConstants {

            @Test
            void success() throws PMException {
                StringValue const1 = new StringValue("test1");
                StringValue const2 = new StringValue("test2");

                pap.policy().userDefinedPML().createConstant("const1", const1);
                pap.policy().userDefinedPML().createConstant("const2", const2);

                Map<String, Value> constants = pap.policy().userDefinedPML().getConstants();
                assertTrue(constants.containsKey("const1"));
                Value actual = constants.get("const1");
                assertEquals(const1, actual);

                assertTrue(constants.containsKey("const2"));
                actual = constants.get("const2");
                assertEquals(const2, actual);
            }
        }


        @Nested
        class GetConstant {

            @Test
            void testPMLConstantNotDefinedException() {
                assertThrows(PMLConstantNotDefinedException.class, () -> pap.policy().userDefinedPML().getConstant("const1"));
            }

            @Test
            void success() throws PMException {
                StringValue const1 = new StringValue("test1");
                StringValue const2 = new StringValue("test2");

                pap.policy().userDefinedPML().createConstant("const1", const1);
                pap.policy().userDefinedPML().createConstant("const2", const2);

                Map<String, Value> constants = pap.policy().userDefinedPML().getConstants();
                assertTrue(constants.containsKey("const1"));
                Value actual = constants.get("const1");
                assertEquals(const1, actual);

                assertTrue(constants.containsKey("const2"));
                actual = constants.get("const2");
                assertEquals(const2, actual);
            }
        }

        @Nested
        class TxTests {

            @Test
            void testSimple() throws PMException {
                pap.beginTx();
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.rollback();
                assertFalse(pap.policy().graph().nodeExists("pc1"));

                pap.beginTx();
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.commit();
                assertTrue(pap.policy().graph().nodeExists("pc1"));
            }

            @Test
            void testSuccess() throws PMException {
                pap.runTx((tx) -> {
                    pap.policy().graph().setResourceAccessRights(new AccessRightSet("read"));
                    pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                    pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                    pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                    pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));
                    pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                    pap.policy().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                                              new AccessRightSet("read"), true,
                                              new ContainerCondition("oa1", false)
                    );

                    pap.policy().obligations().create(new UserContext("u1"), "obl1");

                    pap.policy().userDefinedPML().createConstant("const1", new StringValue("value"));
                });

                assertEquals(new AccessRightSet("read"), pap.policy().graph().getResourceAccessRights());
                assertTrue(pap.policy().graph().nodeExists("pc1"));
                assertTrue(pap.policy().graph().nodeExists("ua1"));
                assertTrue(pap.policy().graph().nodeExists("oa1"));
                assertTrue(pap.policy().graph().nodeExists("u1"));
                assertEquals(
                        new Association("ua1", "oa1", new AccessRightSet("read")),
                        pap.policy().graph().getAssociationsWithSource("ua1").get(0)
                );
                assertTrue(pap.policy().prohibitions().exists("deny-ua1"));
                assertTrue(pap.policy().obligations().exists("obl1"));
                assertTrue(pap.policy().userDefinedPML().getConstants().containsKey("const1"));
            }

            @Test
            void testRollbackGraph() throws PMException {
                assertThrows(PMException.class, () -> pap.runTx((tx) -> {
                    pap.policy().graph().setResourceAccessRights(new AccessRightSet("read"));
                    pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                    pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                    pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                    pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));
                    pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                    pap.policy().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                                              new AccessRightSet("read"), true,
                                              new ContainerCondition("oa1", false)
                    );

                    pap.policy().obligations().create(new UserContext("u1"), "obl1");

                    pap.policy().userDefinedPML().createConstant("const1", new StringValue("value"));

                    pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                }));

                assertEquals(new AccessRightSet(), pap.policy().graph().getResourceAccessRights());
                assertFalse(pap.policy().graph().nodeExists("pc1"));
                assertFalse(pap.policy().graph().nodeExists("ua1"));
                assertFalse(pap.policy().graph().nodeExists("oa1"));
                assertFalse(pap.policy().graph().nodeExists("u1"));
                assertFalse(pap.policy().prohibitions().exists("deny-ua1"));
                assertFalse(pap.policy().obligations().exists("obl1"));
                assertFalse(pap.policy().userDefinedPML().getConstants().containsKey("const1"));
            }

            @Test
            void testRollbackProhibitions() throws PMException {
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read"));
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.policy().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                                          new AccessRightSet("read"), true,
                                          new ContainerCondition("oa1", false)
                );

                pap.policy().userDefinedPML().createConstant("const1", new StringValue("value"));

                assertThrows(PMException.class, () -> {
                    pap.runTx((tx) -> {
                        pap.policy().graph().createPolicyClass("pc2", new HashMap<>());
                        pap.policy().prohibitions().delete("deny-ua1");
                        pap.policy().obligations().create(new UserContext("u1"), "obl1");
                        pap.policy().userDefinedPML().createConstant("const2", new StringValue("value"));
                        pap.policy().prohibitions().create("deny-ua1", new ProhibitionSubject("ua2", ProhibitionSubject.Type.USER_ATTRIBUTE),
                                                  new AccessRightSet("read"), true,
                                                  new ContainerCondition("oa1", false)
                        );
                        pap.policy().prohibitions().create("deny-ua2", new ProhibitionSubject("ua2", ProhibitionSubject.Type.USER_ATTRIBUTE),
                                                  new AccessRightSet("read"), true,
                                                  new ContainerCondition("oa1", false)
                        );

                        pap.policy().prohibitions().create("deny-ua1", new ProhibitionSubject("ua2", ProhibitionSubject.Type.USER_ATTRIBUTE),
                                                  new AccessRightSet("read"), true,
                                                  new ContainerCondition("oa1", false)
                        );
                    });
                });

                assertEquals(new AccessRightSet("read"), pap.policy().graph().getResourceAccessRights());
                assertTrue(pap.policy().graph().nodeExists("pc1"));
                assertTrue(pap.policy().graph().nodeExists("ua1"));
                assertTrue(pap.policy().graph().nodeExists("oa1"));
                assertTrue(pap.policy().graph().nodeExists("u1"));
                assertTrue(pap.policy().prohibitions().exists("deny-ua1"));
                assertFalse(pap.policy().prohibitions().exists("deny-ua2"));
                assertEquals("ua1", pap.policy().prohibitions().get("deny-ua1").getSubject().getName());
                assertFalse(pap.policy().obligations().exists("obl1"));
                assertTrue(pap.policy().userDefinedPML().getConstants().containsKey("const1"));
                assertFalse(pap.policy().userDefinedPML().getConstants().containsKey("const2"));
            }

            @Test
            void testRollbackObligations() throws PMException {
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read"));
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
                pap.policy().graph().createUser("u2", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(new UserContext("u1"), "obl1");

                pap.policy().userDefinedPML().createConstant("const1", new StringValue("value"));

                assertThrows(PMException.class, () -> {
                    pap.runTx((tx) -> {
                        pap.policy().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                                                  new AccessRightSet("read"), true,
                                                  new ContainerCondition("oa1", false)
                        );
                        pap.policy().graph().createUser("u3", new HashMap<>(), List.of("ua1"));
                        pap.policy().obligations().delete("obl1");
                        pap.policy().obligations().create(new UserContext("u2"), "obl1");
                        pap.policy().obligations().create(new UserContext("u1"), "obl2");

                        pap.policy().obligations().create(new UserContext("u1"), "obl1");
                    });
                });

                assertEquals(new AccessRightSet("read"), pap.policy().graph().getResourceAccessRights());
                assertTrue(pap.policy().graph().nodeExists("pc1"));
                assertTrue(pap.policy().graph().nodeExists("ua1"));
                assertTrue(pap.policy().graph().nodeExists("oa1"));
                assertTrue(pap.policy().graph().nodeExists("u1"));
                assertFalse(pap.policy().graph().nodeExists("u3"));
                assertFalse(pap.policy().prohibitions().exists("deny-ua1"));
                assertTrue(pap.policy().obligations().exists("obl1"));
                assertFalse(pap.policy().obligations().exists("obl2"));
                assertEquals("u1", pap.policy().obligations().get("obl1").getAuthor().getUser());
                assertTrue(pap.policy().userDefinedPML().getConstants().containsKey("const1"));
            }

            @Test
            void testRollbackUserDefinedPML() throws PMException {
                pap.policy().graph().setResourceAccessRights(new AccessRightSet("read"));
                pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
                pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
                pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));
                pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
                pap.policy().graph().createUser("u2", new HashMap<>(), List.of("ua1"));

                pap.policy().obligations().create(new UserContext("u1"), "obl1");

                pap.policy().userDefinedPML().createConstant("const1", new StringValue("value"));

                assertThrows(PMException.class, () -> {
                    pap.runTx((tx) -> {
                        pap.policy().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                                                  new AccessRightSet("read"), true,
                                                  new ContainerCondition("oa1", false)
                        );
                        pap.policy().graph().createUser("u3", new HashMap<>(), List.of("ua1"));
                        pap.policy().obligations().delete("obl1");
                        pap.policy().obligations().create(new UserContext("u2"), "obl1");

                        pap.policy().userDefinedPML().createConstant("const2", new StringValue("value"));
                        pap.policy().userDefinedPML().createConstant("const1", new StringValue("value"));
                    });
                });

                assertEquals(new AccessRightSet("read"), pap.policy().graph().getResourceAccessRights());
                assertTrue(pap.policy().graph().nodeExists("pc1"));
                assertTrue(pap.policy().graph().nodeExists("ua1"));
                assertTrue(pap.policy().graph().nodeExists("oa1"));
                assertTrue(pap.policy().graph().nodeExists("u1"));
                assertFalse(pap.policy().graph().nodeExists("u3"));
                assertFalse(pap.policy().prohibitions().exists("deny-ua1"));
                assertTrue(pap.policy().obligations().exists("obl1"));
                assertFalse(pap.policy().obligations().exists("obl2"));
                assertEquals("u1", pap.policy().obligations().get("obl1").getAuthor().getUser());
                assertTrue(pap.policy().userDefinedPML().getConstants().containsKey("const1"));
                assertFalse(pap.policy().userDefinedPML().getConstants().containsKey("const2"));
            }
        }

    }
}