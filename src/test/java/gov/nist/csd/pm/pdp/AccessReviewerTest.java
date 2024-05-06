package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.audit.Explain;
import gov.nist.csd.pm.pap.audit.Path;
import gov.nist.csd.pm.pap.audit.PolicyClass;
import gov.nist.csd.pm.common.graph.relationship.Association;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.query.AccessQuery;
import gov.nist.csd.pm.common.serialization.pml.PMLDeserializer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class AccessReviewerTest {

    private static final String pc1 = "pc1";
    private static final String oa1 = "oa1";
    private static final String oa2 = "oa2";
    private static final String oa5 = "oa5";
    private static final String ua1 = "ua1";
    private static final String o1 = "o1";
    private static final String o2 = "o2";
    private static final String o3 = "o3";
    private static final String u1 = "u1";
    private static final AccessRightSet arset = new AccessRightSet("read", "write");

    public abstract TestContext initTest() throws PMException;

    public record TestContext(AccessQuery accessReviewer, PolicyModification policyModification) {}

    private static final AccessRightSet RWE = new AccessRightSet("read", "write", "execute");

    @Test
    void testComputeAccessibleChildren() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    uas {
                        "ua1"
                    }
                    oas {
                        "oa1"
                            "oa2"                           
                    }
                    associations {
                        "ua1" and "oa1" with ["read", "write"]
                    }
                }
                
                create u "u1" assign to ["ua1"]
                create o "o1" assign to ["oa1"]
                create o "o2" assign to ["oa1"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        List<String> actual = testCtx.accessReviewer().computeAccessibleChildren(new UserContext("u1"), "oa1");
        assertEquals(
                Set.of("oa2", "o1", "o2"),
                new HashSet<>(actual)
        );
    }

    @Test
    void testComputeAccessibleParents() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
               set resource access rights ["read", "write"]
               create pc "pc1" {
                   uas {
                       "ua1"
                   }
                   oas {
                       "oa1"
                       "oa2"
                       "oa3"                           
                   }
                   associations {
                       "ua1" and "oa1" with ["read", "write"]
                       "ua1" and "oa2" with ["read", "write"]
                   }
               }
                               
               create u "u1" assign to ["ua1"]
               create o "o1" assign to ["oa1", "oa2"]
               """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        List<String> actual = testCtx.accessReviewer().computeAccessibleParents(new UserContext("u1"), "o1");
        assertEquals(
                Set.of("oa1", "oa2"),
                new HashSet<>(actual)
        );
    }

    @Test
    void testBuildPOS() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    uas {
                        "ua1"
                    }
                    oas {
                        "oa1"                        
                    }
                    associations {
                        "ua1" and "oa1" with ["read", "write"]
                    }
                }
                create pc "pc2" {
                    uas {
                        "ua2"
                    }
                    oas {
                        "oa2"
                            "oa3"
                        "oa4"                           
                    }
                    associations {
                        "ua2" and "oa2" with ["read", "write"]
                        "ua2" and "oa4" with ["read"]
                    }
                }
                
                create u "u1" assign to ["ua1", "ua2"]
                create o "o1" assign to ["oa1", "oa3"]
                create o "o2" assign to ["oa4"]
                
                create prohibition "p1"
                deny user "u1" 
                access rights ["write"]
                on union of ["oa1"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        Set<String> u1 = testCtx.accessReviewer().buildPOS(new UserContext("u1"));
        assertEquals(
                Set.of("oa1", "oa2", "oa4"),
                u1
        );
    }

    @Test
    void testExplain() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    uas {
                        "ua1"
                    }
                    oas {
                        "oa1"                        
                    }
                    associations {
                        "ua1" and "oa1" with ["read", "write"]
                    }
                }
                create pc "pc2" {
                    uas {
                        "ua2"
                    }
                    oas {
                        "oa2"
                            "oa3"                           
                    }
                    associations {
                        "ua2" and "oa2" with ["read", "write"]
                    }
                }
                
                create u "u1" assign to ["ua1", "ua2"]
                create o "o1" assign to ["oa1", "oa3"]
                
                create prohibition "p1"
                deny user "u1" 
                access rights ["write"]
                on union of ["oa1"]
                
                create prohibition "p2"
                deny user "u1" 
                access rights ["write"]
                on union of [!"oa1"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        Explain explain = testCtx.accessReviewer().explain(new UserContext("u1"), "o1");
        Explain expected = new Explain(
                new AccessRightSet("read"),
                Map.of(
                        "pc1", new PolicyClass(
                                new AccessRightSet("read", "write"),
                                Set.of(new Path(
                                        List.of("u1", "ua1", "oa1"),
                                        List.of("o1", "oa1", "pc1"),
                                        new Association("ua1", "oa1", new AccessRightSet("read", "write"))
                                ))),
                        "pc2", new PolicyClass(
                                new AccessRightSet("read", "write"),
                                Set.of(new Path(
                                        List.of("u1", "ua2", "oa2"),
                                        List.of("o1", "oa3", "oa2", "pc2"),
                                        new Association("ua2", "oa2", new AccessRightSet("read", "write"))
                                )))
                ),
                new AccessRightSet("write"),
                List.of(
                        new Prohibition("p1", new ProhibitionSubject("u1", ProhibitionSubject.Type.USER), new AccessRightSet("write"), false, List.of(new ContainerCondition("oa1", false)))
                )
        );
        assertEquals(expected, explain);
    }

    @Test
    void testExplainOnObjAttrWithAssociation() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    user attributes {
                        "ua1"
                    }
                    
                    object attributes {
                        "oa1"
                            "oa2"
                    }
                    
                    associations {
                        "ua1" and "oa1" with ["write"]
                        "ua1" and "oa2" with ["read"]
                    }
                }
                
                create user "u1" assign to ["ua1"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());
        Explain actual = testCtx.accessReviewer().explain(new UserContext("u1"), "oa2");
        assertEquals(
                new Explain(
                        new AccessRightSet("read", "write"),
                        Map.of(
                                "pc1", new PolicyClass(
                                        new AccessRightSet("read", "write"),
                                        Set.of(
                                                new Path(
                                                        List.of("u1", "ua1", "oa1"),
                                                        List.of("oa2", "oa1", "pc1"),
                                                        new Association("ua1", "oa1", new AccessRightSet("write"))
                                                ),
                                                new Path(
                                                        List.of("u1", "ua1", "oa2"),
                                                        List.of("oa2", "oa1", "pc1"),
                                                        new Association("ua1", "oa2", new AccessRightSet("read"))
                                                )
                                        )
                                )
                        ),
                        new AccessRightSet(),
                        List.of()
                ),
                actual
        );
    }

    @Test
    void testComputeSubgraphPrivileges() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    uas {
                        "ua1"
                    }
                    oas {
                        "oa1"
                            "oa2"                           
                    }
                    associations {
                        "ua1" and "oa1" with ["read", "write"]
                    }
                }
                
                create u "u1" assign to ["ua1"]
                create o "o1" assign to ["oa2"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());
        Map<String, AccessRightSet> u1 =
                testCtx.accessReviewer().computeSubgraphPrivileges(new UserContext("u1"), "oa1");
        assertEquals(
                Map.of(
                        "oa2", new AccessRightSet("read", "write"),
                        "o1", new AccessRightSet("read", "write")
                ),
                u1
        );
    }

    @Test
    void testFindBorderAttributes() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    uas {
                        "ua1"
                        "ua2"
                    }
                    oas {
                        "oa1"
                            "oa2"
                    }
                    associations {
                        "ua1" and "oa1" with ["read", "write"]
                        "ua2" and "oa2" with ["read"]
                    }
                }
                
                create u "u1" assign to ["ua1", "ua2"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());
        Map<String, AccessRightSet> u1 = testCtx.accessReviewer().findBorderAttributes("u1");
        assertEquals(
                Map.of(
                        "oa1", new AccessRightSet("read", "write"),
                        "oa2", new AccessRightSet("read")
                ),
                u1
        );
    }

    @Test
    void testBuildACL() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    uas {
                        "ua1"
                        "ua2"
                    }
                    oas {
                        "oa1"
                    }
                    associations {
                        "ua1" and "oa1" with ["read", "write"]
                        "ua2" and "oa1" with ["read"]
                    }
                }
                
                create u "u1" assign to ["ua1"]
                create u "u2" assign to ["ua2"]
                create o "o1" assign to ["oa1"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());
        Map<String, AccessRightSet> o1 = testCtx.accessReviewer().buildACL("o1");
        assertEquals(
                Map.of(
                        "u1", new AccessRightSet("read", "write"),
                        "u2", new AccessRightSet("read")
                ),
                o1
        );
    }

    @Test
    void testBuildCapabilityList() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    uas {
                        "ua1"
                    }
                    oas {
                        "oa1"
                        "oa2"
                    }
                    associations {
                        "ua1" and "oa1" with ["read", "write"]
                        "ua1" and "oa2" with ["read"]
                    }
                }
                
                create u "u1" assign to ["ua1"]
                create o "o1" assign to ["oa1"]
                create o "o2" assign to ["oa2"]
                
                create prohibition "p1"
                deny user "u1" 
                access rights ["write"]
                on union of ["oa1"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());
        Map<String, AccessRightSet> u1 = testCtx.accessReviewer().buildCapabilityList(new UserContext("u1"));
        assertEquals(
                Map.of(
                        "o1", new AccessRightSet("read"),
                        "o2", new AccessRightSet("read"),
                        "oa1", new AccessRightSet("read"),
                        "oa2", new AccessRightSet("read")
                ),
                u1
        );
    }

    @Test
    void testComputeDeniedPrivileges() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    uas {
                        "ua1"
                    }
                    oas {
                        "oa1"
                    }
                    associations {
                        "ua1" and "oa1" with ["read", "write"]
                    }
                }
                
                create u "u1" assign to ["ua1"]
                create o "o1" assign to ["oa1"]
                
                create prohibition "p1"
                deny user "u1" 
                access rights ["write"]
                on union of ["oa1"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());
        AccessRightSet deniedPrivileges = testCtx.accessReviewer().computeDeniedPrivileges(new UserContext("u1"), "o1");
        assertEquals(new AccessRightSet("write"), deniedPrivileges);
    }

    @Test
    void testComputePolicyClassAccessRights() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    uas {
                        "ua1"
                    }
                    oas {
                        "oa1"
                    }
                    associations {
                        "ua1" and "oa1" with ["read", "write"]
                    }
                }
                create pc "pc2" {
                    uas {
                        "ua2"
                    }
                    oas {
                        "oa2"
                    }
                    associations {
                        "ua2" and "oa2" with ["read"]
                    }
                }
                
                create u "u1" assign to ["ua1", "ua2"]
                create o "o1" assign to ["oa1", "oa2"]
                """;
        testCtx.policyModification().deserialize(new UserContext("u1"), pml, new PMLDeserializer());
        Map<String, AccessRightSet> policyClassAccessRights =
                testCtx.accessReviewer().computePolicyClassAccessRights(new UserContext("u1"), "o1");
        assertEquals(
                Map.of(
                        "pc1", new AccessRightSet("read", "write"),
                        "pc2", new AccessRightSet("read")
                ),
                policyClassAccessRights
        );
    }

    @Test
    void testGetChildren() throws PMException {
        TestContext testCtx = initTest();

        testCtx.policyModification().graph().setResourceAccessRights(RWE);

        String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
        String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
        String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
        String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
        String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));
        String o2 = testCtx.policyModification().graph().createObject("o2", new HashMap<>(), List.of(oa1));
        String o3 = testCtx.policyModification().graph().createObject("o3", new HashMap<>(), List.of(oa1));

        AccessRightSet arset = new AccessRightSet("read", "write");
        testCtx.policyModification().graph().associate(ua1, oa1, arset);
        Map<String, AccessRightSet> subgraph = testCtx.accessReviewer().computeSubgraphPrivileges(new UserContext(u1), oa1);
        assertEquals(
                Map.of("o1", arset, "o2", arset, "o3", arset),
                subgraph
        );
    }

    @Test
    void testGetAccessibleNodes() throws PMException {
        TestContext testCtx = initTest();
        testCtx.policyModification().graph().setResourceAccessRights(RWE);

        String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
        String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
        String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
        String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
        String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));
        String o2 = testCtx.policyModification().graph().createObject("o2", new HashMap<>(), List.of(oa1));
        String o3 = testCtx.policyModification().graph().createObject("o3", new HashMap<>(), List.of(oa1));

        AccessRightSet arset = new AccessRightSet("read", "write");
        testCtx.policyModification().graph().associate(ua1, oa1, arset);
        Map<String, AccessRightSet> accessibleNodes = testCtx.accessReviewer().buildCapabilityList(new UserContext(u1));

        assertTrue(accessibleNodes.containsKey(oa1));
        assertTrue(accessibleNodes.containsKey(o1));
        assertTrue(accessibleNodes.containsKey(o2));
        assertTrue(accessibleNodes.containsKey(o3));

        assertEquals(arset, accessibleNodes.get(oa1));
        assertEquals(arset, accessibleNodes.get(o1));
        assertEquals(arset, accessibleNodes.get(o2));
        assertEquals(arset, accessibleNodes.get(o3));
    }

    @Nested
    class GetPrivilegesTests {

        @Test
        void testGraph1() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read", "write"));
            assertTrue(
                    testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read", "write")));
        }

        @Test
        void testGraph2() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1, pc2));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1, ua2));

            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));


            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).isEmpty());
        }

        @Test
        void testGraph3() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read", "write"));


            assertTrue(
                    testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read", "write")));
        }

        @Test
        void testGraph4() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of());
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of());
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1, ua2));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));
            testCtx.policyModification().graph().associate(ua2, oa1, new AccessRightSet("write"));


            assertEquals(
                    new AccessRightSet("read", "write"),
                    testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1)
            );
        }

        @Test
        void testGraph5() throws PMException {
            TestContext testCtx = initTest();
            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc2));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1, ua2));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));
            testCtx.policyModification().graph().associate(ua2, oa2, new AccessRightSet("read", "write"));



            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read")));
        }

        @Test
        void testGraph6() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc2));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1, ua2));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read", "write"));
            testCtx.policyModification().graph().associate(ua2, oa2, new AccessRightSet("read"));



            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read")));
        }

        @Test
        void testGraph7() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read", "write"));



            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).isEmpty());
        }

        @Test
        void testGraph8() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("*"));



            Set<String> list = testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1);
            assertTrue(list.containsAll(allAdminAccessRights()));
            assertTrue(list.containsAll(RWE));
        }

        @Test
        void testGraph9() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("*"));
            testCtx.policyModification().graph().associate(ua2, oa1, new AccessRightSet("read", "write"));



            Set<String> list = testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1);
            assertTrue(list.containsAll(allAdminAccessRights()));
            assertTrue(list.containsAll(RWE));
        }

        @Test
        void testGraph10() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc2));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1, ua2));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("*"));
            testCtx.policyModification().graph().associate(ua2, oa2, new AccessRightSet("read", "write"));



            assertTrue(
                    testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read", "write")));
        }

        @Test
        void testGraph11() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("*"));



            assertEquals(new AccessRightSet(), testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1));
        }

        @Test
        void testGraph12() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1, ua2));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));
            testCtx.policyModification().graph().associate(ua2, oa1, new AccessRightSet("write"));



            assertTrue(
                    testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read", "write")));
        }

        @Test
        void testGraph13() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(ua2));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(oa2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("*"));
            testCtx.policyModification().graph().associate(ua2, oa2, new AccessRightSet("read"));



            Set<String> list = testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1);
            assertTrue(list.containsAll(allAdminAccessRights()));
            assertTrue(list.contains("read"));
        }

        @Test
        void testGraph14() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1, ua2));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1, pc2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("*"));
            testCtx.policyModification().graph().associate(ua2, oa1, new AccessRightSet("*"));



            Set<String> list = testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1);
            assertTrue(list.containsAll(allAdminAccessRights()));
            assertTrue(list.containsAll(RWE));
        }

        @Test
        void testGraph15() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(ua2));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(oa2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("*"));
            testCtx.policyModification().graph().associate(ua2, oa2, new AccessRightSet("read"));



            Set<String> list = testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1);
            assertTrue(list.containsAll(allAdminAccessRights()));
            assertTrue(list.containsAll(RWE));
        }

        @Test
        void testGraph16() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(ua2));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));
            testCtx.policyModification().graph().associate(ua2, oa1, new AccessRightSet("write"));



            assertTrue(
                    testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read", "write")));
        }

        // removed graph7 due to adding the parent IDs to the createNode, need to always connect to the testCtx.policy().graph().

        @Test
        void testGraph18() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read", "write"));



            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).isEmpty());
        }

        @Test
        void testGraph19() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua2));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));



            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).isEmpty());
        }

        @Test
        void testGraph20() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1, ua2));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));
            testCtx.policyModification().graph().associate(ua2, oa2, new AccessRightSet("read", "write"));



            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read")));
        }

        @Test
        void testGraph21() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1, ua2));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));
            testCtx.policyModification().graph().associate(ua2, oa2, new AccessRightSet("write"));



            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).isEmpty());
        }

        @Test
        void testGraph22() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String pc2 = testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read", "write"));



            assertTrue(
                    testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read", "write")));
        }

        @Test
        void testGraph23WithProhibitions() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa3 = testCtx.policyModification().graph().createObjectAttribute("oa3", new HashMap<>(), List.of(pc1));
            String oa4 = testCtx.policyModification().graph().createObjectAttribute("oa4", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(oa3));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(oa4));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa3, new AccessRightSet("read", "write", "execute"));
            testCtx.policyModification().prohibitions().create("deny", ProhibitionSubject.userAttribute("ua1"), new AccessRightSet("read"), true,
                                         new ContainerCondition(oa1, false),
                                         new ContainerCondition(oa2, false)
            );

            testCtx.policyModification().prohibitions().create("deny2", ProhibitionSubject.user(u1), new AccessRightSet("write"),
                                         true,
                                         new ContainerCondition(oa3, false)
            );


            Set<String> list = testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1);
            assertEquals(1, list.size());
            assertTrue(list.contains("execute"));
        }

        @Test
        void testGraph24WithProhibitions() throws PMException {
            TestContext testCtx = initTest();
            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));
            String o2 = testCtx.policyModification().graph().createObject("o2", new HashMap<>(), List.of(oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));

            testCtx.policyModification().prohibitions().create("deny", ProhibitionSubject.userAttribute(ua1),
                                         new AccessRightSet("read"),
                                         true,
                                         new ContainerCondition(oa1, false),
                                         new ContainerCondition(oa2, true)
            );


            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).contains("read"));
            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o2).isEmpty());

            testCtx.policyModification().graph().associate(ua1, oa2, new AccessRightSet("read"));

            testCtx.policyModification().prohibitions().create("deny-process", ProhibitionSubject.process("1234"),
                                                   new AccessRightSet("read"),
                                                   false,
                                                   new ContainerCondition(oa1, false)
            );

            assertEquals(
                    new AccessRightSet(),
                    testCtx.accessReviewer().computePrivileges(new UserContext(u1, "1234"), o1)
            );
        }

        @Test
        void testGraph25WithProhibitions() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(oa1));
            String oa3 = testCtx.policyModification().graph().createObjectAttribute("oa3", new HashMap<>(), List.of(oa1));
            String oa4 = testCtx.policyModification().graph().createObjectAttribute("oa4", new HashMap<>(), List.of(oa3));
            String oa5 = testCtx.policyModification().graph().createObjectAttribute("oa5", new HashMap<>(), List.of(oa2));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa4));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read", "write"));

            testCtx.policyModification().prohibitions().create("deny", ProhibitionSubject.user(u1), new AccessRightSet("read", "write"),
                                         true,
                                         new ContainerCondition(oa4, true),
                                         new ContainerCondition(oa1, false)
            );


            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), oa5).isEmpty());
            assertTrue(
                    testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).containsAll(Arrays.asList("read", "write")));
        }

        @Test
        void testGraph25WithProhibitions2() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read", "write"));


            testCtx.policyModification().prohibitions().create("deny", ProhibitionSubject.user(u1), new AccessRightSet("read", "write"),
                                         true,
                                         new ContainerCondition(oa1, false),
                                         new ContainerCondition(oa2, false)
            );


            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(u1), o1).isEmpty());
        }

        @Test
        void testDeciderWithUA() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua2 = testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of(pc1));
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(ua2));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String oa2 = testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1, oa2));
            String o2 = testCtx.policyModification().graph().createObject("o2", new HashMap<>(), List.of(oa2));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet("read"));
            testCtx.policyModification().graph().associate(ua2, oa1, new AccessRightSet("write"));


            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext(ua1), oa1)
                              .containsAll(Arrays.asList("read", "write")));
        }

        @Test
        void testProhibitionsAllCombinations() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createObjectAttribute("oa2", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createObjectAttribute("oa4", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of("oa1", "oa2", "oa3"));
            testCtx.policyModification().graph().createObject("o2", new HashMap<>(), List.of("oa1", "oa4"));

            testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
            testCtx.policyModification().graph().createUser("u2", new HashMap<>(), List.of("ua1"));
            testCtx.policyModification().graph().createUser("u3", new HashMap<>(), List.of("ua1"));
            testCtx.policyModification().graph().createUser("u4", new HashMap<>(), List.of("ua1"));

            testCtx.policyModification().graph().associate("ua1", "oa1", new AccessRightSet("read", "write"));


            testCtx.policyModification().prohibitions().create(
                    "p1",
                    ProhibitionSubject.user("u1"),
                    new AccessRightSet("write"),
                    true,
                    new ContainerCondition("oa1", false),
                    new ContainerCondition("oa2", false),
                    new ContainerCondition("oa3", false)
            );

            testCtx.policyModification().prohibitions().create(
                    "p2",
                    ProhibitionSubject.user("u2"),
                    new AccessRightSet("write"),
                    false,
                    new ContainerCondition("oa1", false),
                    new ContainerCondition("oa2", false),
                    new ContainerCondition("oa3", false)
            );

            testCtx.policyModification().prohibitions().create(
                    "p3",
                    ProhibitionSubject.user("u3"),
                    new AccessRightSet("write"),
                    true,
                    new ContainerCondition("oa1", false),
                    new ContainerCondition("oa2", true)
            );

            testCtx.policyModification().prohibitions().create(
                    "p4",
                    ProhibitionSubject.user("u4"),
                    new AccessRightSet("write"),
                    false,
                    new ContainerCondition("oa1", false),
                    new ContainerCondition("oa2", true)
            );

            testCtx.policyModification().prohibitions().create(
                    "p5",
                    ProhibitionSubject.user("u4"),
                    new AccessRightSet("write"),
                    false,
                    new ContainerCondition("oa2", true)
            );


            Set<String> list = testCtx.accessReviewer().computePrivileges(new UserContext("u1"), "o1");
            assertTrue(list.contains("read") && !list.contains("write"));

            list = testCtx.accessReviewer().computePrivileges(new UserContext("u1"), "o2");
            assertTrue(list.contains("read") && list.contains("write"));

            list = testCtx.accessReviewer().computePrivileges(new UserContext("u2"), "o2");
            assertTrue(list.contains("read") && !list.contains("write"));

            list = testCtx.accessReviewer().computePrivileges(new UserContext("u3"), "o2");
            assertTrue(list.contains("read") && !list.contains("write"));

            list = testCtx.accessReviewer().computePrivileges(new UserContext("u4"), "o1");
            assertTrue(list.contains("read") && !list.contains("write"));

            list = testCtx.accessReviewer().computePrivileges(new UserContext("u4"), "o2");
            assertTrue(list.contains("read") && !list.contains("write"));
        }

        @Test
        void testPermissions() throws PMException {
            TestContext testCtx = initTest();
            testCtx.policyModification().graph().setResourceAccessRights(RWE);

            String pc1 = testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            String ua1 = testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of(pc1));
            String u1 = testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of(ua1));
            String oa1 = testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of(pc1));
            String o1 = testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of(oa1));

            testCtx.policyModification().graph().associate(ua1, oa1, allAccessRights());


            Set<String> list = testCtx.accessReviewer().computePrivileges(new UserContext("u1"), "o1");
            assertTrue(list.containsAll(allAdminAccessRights()));
            assertTrue(list.containsAll(RWE));

            testCtx.policyModification().graph().associate(ua1, oa1, allAdminAccessRights());
            list = testCtx.accessReviewer().computePrivileges(new UserContext("u1"), "o1");
            assertTrue(list.containsAll(allAdminAccessRights()));
            assertFalse(list.containsAll(RWE));

            testCtx.policyModification().graph().associate(ua1, oa1, new AccessRightSet(ALL_RESOURCE_ACCESS_RIGHTS));
            list = testCtx.accessReviewer().computePrivileges(new UserContext("u1"), "o1");
            assertFalse(list.containsAll(allAdminAccessRights()));
            assertTrue(list.containsAll(RWE));
        }

        @Test
        void testPermissionsInOnlyOnePC() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(RWE);
            testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            testCtx.policyModification().graph().createPolicyClass("pc2", new HashMap<>());
            testCtx.policyModification().graph().createUserAttribute("ua3", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createUserAttribute("ua2", new HashMap<>(), List.of("ua3"));
            testCtx.policyModification().graph().createUserAttribute("u1", new HashMap<>(), List.of("ua2"));

            testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createObjectAttribute("oa3", new HashMap<>(), List.of("pc2"));
            testCtx.policyModification().graph().assign("oa3", "oa1");
            testCtx.policyModification().graph().createObject("o1", new HashMap<>(), List.of("oa3"));

            testCtx.policyModification().graph().associate("ua3", "oa1", new AccessRightSet("read"));


            assertTrue(testCtx.accessReviewer().computePrivileges(new UserContext("u1"), "o1").isEmpty());
        }

        @Test
        void testProhibitionsWithContainerAsTarget() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(new AccessRightSet("read"));
            testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
            testCtx.policyModification().graph().associate("ua1", "oa1", new AccessRightSet("read"));

            testCtx.policyModification().prohibitions().create("deny1", ProhibitionSubject.user("u1"), new AccessRightSet("read"), false,
                                         new ContainerCondition("oa1", false)
            );


            AccessRightSet deniedAccessRights = testCtx.accessReviewer().computeDeniedPrivileges(new UserContext("u1"), "oa1");
            assertTrue(deniedAccessRights.contains("read"));
        }

        @Test
        void testProhibitionWithContainerAsTargetComplement() throws PMException {
            TestContext testCtx = initTest();

            testCtx.policyModification().graph().setResourceAccessRights(new AccessRightSet("read"));
            testCtx.policyModification().graph().createPolicyClass("pc1", new HashMap<>());
            testCtx.policyModification().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            testCtx.policyModification().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
            testCtx.policyModification().graph().associate("ua1", "oa1", new AccessRightSet("read"));

            testCtx.policyModification().prohibitions().create("deny1", ProhibitionSubject.user("u1"), new AccessRightSet("read"), false,
                                         new ContainerCondition("oa1", true)
            );


            AccessRightSet deniedAccessRights = testCtx.accessReviewer().computeDeniedPrivileges(new UserContext("u1"), "oa1");
            assertFalse(deniedAccessRights.contains("read"));
        }
    }

}
