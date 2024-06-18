package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.serialization.json.JSONDeserializer;
import gov.nist.csd.pm.pap.serialization.json.JSONSerializer;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.pap.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.pap.serialization.pml.PMLSerializer;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.util.SamplePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

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
        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().associate("ua1", "oa1", new AccessRightSet());
        pap.commit();

        assertTrue(pap.query().graph().nodeExists("pc1"));
        assertTrue(pap.query().graph().nodeExists("oa1"));
        assertTrue(pap.query().graph().nodeExists("ua1"));
        assertTrue(pap.query().graph().getAssociationsWithSource("ua1").iterator().next()
                .equals(new Association("ua1", "oa1", new AccessRightSet())));

        pap.beginTx();
        pap.modify().graph().deleteNode("ua1");
        pap.rollback();
        assertTrue(pap.query().graph().nodeExists("ua1"));
    }

    @Nested
    class Serialization {

        @Test
        void testErrorDuringDeserializationCausesRollback() throws PMException {
            String pml = """
                    create pc "pc1"
                    create ua "ua1" assign to ["pc2"]
                    """;

            assertThrows(PMException.class, () -> pap.deserialize(new UserContext("u1"), pml, new PMLDeserializer()));
            assertFalse(pap.query().graph().nodeExists("pc1"));
            assertFalse(pap.query().graph().nodeExists("ua1"));
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
                when subject => pAny()
                performs operation => pContainedIn(["event1", "event2"])
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
            pap.deserialize(userContext, input, new PMLDeserializer());

            String pml = pap.serialize(new PMLSerializer());
            MemoryPAP pmlPAP = new MemoryPAP();
            pmlPAP.deserialize(userContext, pml, new PMLDeserializer());

            String json = pmlPAP.serialize(new JSONSerializer());
            MemoryPAP jsonPAP = new MemoryPAP();
            jsonPAP.deserialize(userContext, json, new JSONDeserializer());

            assertPolicyEquals(pap.query(), pmlPAP.query());
            assertPolicyEquals(pap.query(), jsonPAP.query());

            assertThrows(PMException.class, () -> {
                pap.deserialize(new UserContext("unknown user"), input, new PMLDeserializer());
            });
        }
        @Test
        void testJSONAndPMLCreateEqualPolicy() throws PMException {
            UserContext userContext = new UserContext("u1");
            pap.deserialize(userContext, input, new PMLDeserializer());
            String pml = pap.serialize(new PMLSerializer());
            String json = pap.serialize(new JSONSerializer());

            PAP pap1 = new MemoryPAP();
            pap1.deserialize(userContext, pml, new PMLDeserializer());

            PAP pap2 = new MemoryPAP();
            pap2.deserialize(userContext, json, new JSONDeserializer());

            assertPolicyEquals(pap1.query(), pap2.query());
        }

        @Test
        void testAssignPolicyClassTargetToAnotherPolicyClass() throws PMException {
            UserContext userContext = new UserContext("u1");
            pap.deserialize(userContext, input, new PMLDeserializer());

            pap.modify().graph().createObjectAttribute("test-oa", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().assign(AdminPolicy.policyClassTargetName("pc1"), "test-oa");
            String pml = pap.serialize(new PMLSerializer());

            PAP pap1 = new MemoryPAP();
            pap1.deserialize(userContext, pml, new PMLDeserializer());

            assertPolicyEquals(pap.query(), pap1.query());
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
                        policy.modify().graph().createPolicyClass("pc3", new HashMap<>());
                        return new VoidValue();
                    })
                    .build();

            pap.executePML(new UserContext("u1"), "create ua \"ua4\" assign to [\"pc2\"]\ntestfunc()", functionDefinitionStatement);
            assertTrue(pap.query().graph().nodeExists("ua4"));
            assertTrue(pap.query().graph().nodeExists("pc3"));
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
        pap.reset();

        testAdminPolicy(pap, 1);
    }

    public static void testAdminPolicy(PAP pap, int numExpectedPolicyClasses) throws PMException {
        assertTrue(pap.query().graph().nodeExists(AdminPolicyNode.ADMIN_POLICY.nodeName()));
        Collection<String> children = pap.query().graph().getChildren(AdminPolicyNode.ADMIN_POLICY.nodeName());
        assertEquals(5, children.size());
        assertTrue(children.containsAll(List.of(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName(),
                                                AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName(), AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), AdminPolicyNode.OBLIGATIONS_TARGET.nodeName())));

        assertTrue(pap.query().graph().nodeExists(AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName()));
        Collection<String> parents = pap.query().graph().getParents(AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName()));

        assertTrue(pap.query().graph().nodeExists(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName()));
        children = pap.query().graph().getChildren(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName());
        assertEquals(numExpectedPolicyClasses, children.size());
        assertTrue(children.contains(AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName()));

        parents = pap.query().graph().getParents(AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));

        assertTrue(pap.query().graph().nodeExists(AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName()));
        parents = pap.query().graph().getParents(AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));

        assertTrue(pap.query().graph().nodeExists(AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName()));
        parents = pap.query().graph().getParents(AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));

        assertTrue(pap.query().graph().nodeExists(AdminPolicyNode.PROHIBITIONS_TARGET.nodeName()));
        parents = pap.query().graph().getParents(AdminPolicyNode.PROHIBITIONS_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));

        assertTrue(pap.query().graph().nodeExists(AdminPolicyNode.OBLIGATIONS_TARGET.nodeName()));
        parents = pap.query().graph().getParents(AdminPolicyNode.OBLIGATIONS_TARGET.nodeName());
        assertEquals(1, parents.size());
        assertTrue(parents.contains(AdminPolicyNode.ADMIN_POLICY.nodeName()));
    }


    @Nested
    class TxTests {

        @Test
        void testSimple() throws PMException {
            pap.beginTx();
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.rollback();
            assertFalse(pap.query().graph().nodeExists("pc1"));

            pap.beginTx();
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.commit();
            assertTrue(pap.query().graph().nodeExists("pc1"));
        }

        @Test
        void testSuccess() throws PMException {
            pap.runTx((tx) -> {
                pap.modify().graph().setResourceAccessRights(new AccessRightSet("read"));
                pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
                pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));
                pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.modify().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                        new AccessRightSet("read"), true,
                        Collections.singleton(new ContainerCondition("oa1", false))
                );

                pap.modify().obligations().create(new UserContext("u1"), "obl1", List.of());

                pap.modify().pml().createConstant("const1", new StringValue("value"));
            });

            assertEquals(new AccessRightSet("read"), pap.query().graph().getResourceAccessRights());
            assertTrue(pap.query().graph().nodeExists("pc1"));
            assertTrue(pap.query().graph().nodeExists("ua1"));
            assertTrue(pap.query().graph().nodeExists("oa1"));
            assertTrue(pap.query().graph().nodeExists("u1"));
            assertEquals(
                    new Association("ua1", "oa1", new AccessRightSet("read")),
                    pap.query().graph().getAssociationsWithSource("ua1").iterator().next()
            );
            assertTrue(pap.query().prohibitions().exists("deny-ua1"));
            assertTrue(pap.query().obligations().exists("obl1"));
            assertTrue(pap.query().pml().getConstants().containsKey("const1"));
        }

        @Test
        void testRollbackGraph() throws PMException {
            assertThrows(PMException.class, () -> pap.runTx((tx) -> {
                pap.modify().graph().setResourceAccessRights(new AccessRightSet("read"));
                pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
                pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
                pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
                pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));
                pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

                pap.modify().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                        new AccessRightSet("read"), true,
                        Collections.singleton(new ContainerCondition("oa1", false))
                );

                pap.modify().obligations().create(new UserContext("u1"), "obl1", List.of());

                pap.modify().pml().createConstant("const1", new StringValue("value"));

                pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            }));

            assertEquals(new AccessRightSet(), pap.query().graph().getResourceAccessRights());
            assertFalse(pap.query().graph().nodeExists("pc1"));
            assertFalse(pap.query().graph().nodeExists("ua1"));
            assertFalse(pap.query().graph().nodeExists("oa1"));
            assertFalse(pap.query().graph().nodeExists("u1"));
            assertFalse(pap.query().prohibitions().exists("deny-ua1"));
            assertFalse(pap.query().obligations().exists("obl1"));
            assertFalse(pap.query().pml().getConstants().containsKey("const1"));
        }

        @Test
        void testRollbackProhibitions() throws PMException {
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read"));
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));
            pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));

            pap.modify().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                    new AccessRightSet("read"), true,
                    Collections.singleton(new ContainerCondition("oa1", false))
            );

            pap.modify().pml().createConstant("const1", new StringValue("value"));

            assertThrows(PMException.class, () -> {
                pap.runTx((tx) -> {
                    pap.modify().graph().createPolicyClass("pc2", new HashMap<>());
                    pap.modify().prohibitions().delete("deny-ua1");
                    pap.modify().obligations().create(new UserContext("u1"), "obl1", List.of());
                    pap.modify().pml().createConstant("const2", new StringValue("value"));
                    pap.modify().prohibitions().create("deny-ua1", new ProhibitionSubject("ua2", ProhibitionSubject.Type.USER_ATTRIBUTE),
                            new AccessRightSet("read"), true,
                            Collections.singleton(new ContainerCondition("oa1", false))
                    );
                    pap.modify().prohibitions().create("deny-ua2", new ProhibitionSubject("ua2", ProhibitionSubject.Type.USER_ATTRIBUTE),
                            new AccessRightSet("read"), true,
                            Collections.singleton(new ContainerCondition("oa1", false))
                    );

                    pap.modify().prohibitions().create("deny-ua1", new ProhibitionSubject("ua2", ProhibitionSubject.Type.USER_ATTRIBUTE),
                            new AccessRightSet("read"), true,
                            Collections.singleton(new ContainerCondition("oa1", false))
                    );
                });
            });

            assertEquals(new AccessRightSet("read"), pap.query().graph().getResourceAccessRights());
            assertTrue(pap.query().graph().nodeExists("pc1"));
            assertTrue(pap.query().graph().nodeExists("ua1"));
            assertTrue(pap.query().graph().nodeExists("oa1"));
            assertTrue(pap.query().graph().nodeExists("u1"));
            assertTrue(pap.query().prohibitions().exists("deny-ua1"));
            assertFalse(pap.query().prohibitions().exists("deny-ua2"));
            assertEquals("ua1", pap.query().prohibitions().get("deny-ua1").getSubject().getName());
            assertFalse(pap.query().obligations().exists("obl1"));
            assertTrue(pap.query().pml().getConstants().containsKey("const1"));
            assertFalse(pap.query().pml().getConstants().containsKey("const2"));
        }

        @Test
        void testRollbackObligations() throws PMException {
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read"));
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));
            pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
            pap.modify().graph().createUser("u2", new HashMap<>(), List.of("ua1"));

            pap.modify().obligations().create(new UserContext("u1"), "obl1", Collections.emptyList());

            pap.modify().pml().createConstant("const1", new StringValue("value"));

            assertThrows(PMException.class, () -> {
                pap.runTx(tx -> {
                    tx.modify().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                            new AccessRightSet("read"), true,
                            Collections.singleton(new ContainerCondition("oa1", false))
                    );
                    tx.modify().graph().createUser("u3", new HashMap<>(), List.of("ua1"));
                    tx.modify().obligations().delete("obl1");
                    tx.modify().obligations().create(new UserContext("u2"), "obl1", List.of());
                    tx.modify().obligations().create(new UserContext("u1"), "obl2", List.of());

                    tx.modify().obligations().create(new UserContext("u1"), "obl1", List.of());
                });
            });

            assertEquals(new AccessRightSet("read"), pap.query().graph().getResourceAccessRights());
            assertTrue(pap.query().graph().nodeExists("pc1"));
            assertTrue(pap.query().graph().nodeExists("ua1"));
            assertTrue(pap.query().graph().nodeExists("oa1"));
            assertTrue(pap.query().graph().nodeExists("u1"));
            assertFalse(pap.query().graph().nodeExists("u3"));
            assertFalse(pap.query().prohibitions().exists("deny-ua1"));
            assertTrue(pap.query().obligations().exists("obl1"));
            assertFalse(pap.query().obligations().exists("obl2"));
            assertEquals("u1", pap.query().obligations().get("obl1").getAuthor().getUser());
            assertTrue(pap.query().pml().getConstants().containsKey("const1"));
        }

        @Test
        void testRollbackUserDefinedPML() throws PMException {
            pap.modify().graph().setResourceAccessRights(new AccessRightSet("read"));
            pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
            pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
            pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));
            pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
            pap.modify().graph().createUser("u2", new HashMap<>(), List.of("ua1"));

            pap.modify().obligations().create(new UserContext("u1"), "obl1", List.of());

            pap.modify().pml().createConstant("const1", new StringValue("value"));

            assertThrows(PMException.class, () -> {
                pap.runTx((tx) -> {
                    pap.modify().prohibitions().create("deny-ua1", new ProhibitionSubject("ua1", ProhibitionSubject.Type.USER_ATTRIBUTE),
                            new AccessRightSet("read"), true,
                            Collections.singleton(new ContainerCondition("oa1", false))
                    );
                    pap.modify().graph().createUser("u3", new HashMap<>(), List.of("ua1"));
                    pap.modify().obligations().delete("obl1");
                    pap.modify().obligations().create(new UserContext("u2"), "obl1", List.of());

                    pap.modify().pml().createConstant("const2", new StringValue("value"));
                    pap.modify().pml().createConstant("const1", new StringValue("value"));
                });
            });

            assertEquals(new AccessRightSet("read"), pap.query().graph().getResourceAccessRights());
            assertTrue(pap.query().graph().nodeExists("pc1"));
            assertTrue(pap.query().graph().nodeExists("ua1"));
            assertTrue(pap.query().graph().nodeExists("oa1"));
            assertTrue(pap.query().graph().nodeExists("u1"));
            assertFalse(pap.query().graph().nodeExists("u3"));
            assertFalse(pap.query().prohibitions().exists("deny-ua1"));
            assertTrue(pap.query().obligations().exists("obl1"));
            assertFalse(pap.query().obligations().exists("obl2"));
            assertEquals("u1", pap.query().obligations().get("obl1").getAuthor().getUser());
            assertTrue(pap.query().pml().getConstants().containsKey("const1"));
            assertFalse(pap.query().pml().getConstants().containsKey("const2"));
        }
    }

}