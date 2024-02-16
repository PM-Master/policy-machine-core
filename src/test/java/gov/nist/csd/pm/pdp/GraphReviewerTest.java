package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.memory.MemoryPolicyStore;
import gov.nist.csd.pm.policy.Policy;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.UserContext;
import gov.nist.csd.pm.policy.review.AccessReview;
import gov.nist.csd.pm.policy.review.GraphReview;
import gov.nist.csd.pm.policy.serialization.pml.PMLDeserializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class GraphReviewerTest {

    public abstract TestContext initTest() throws PMException;

    public record TestContext(GraphReview graphReviewer, Policy policy) {}

    @Test
    void testGetAttributeContainers() throws PMException {
        TestContext testCtx = initTest();
        String pml =
                """
                set resource access rights ["read", "write"]
                create pc "pc1" {
                    oas {
                        "oa1"
                            "oa2"
                                "oa3"
                        "oa4"                                  
                    }
                }
                                
                create pc "pc2" {
                    oas {
                        "oa5"
                            "oa6"
                    }
                }
                                
                create pc "pc3"
                                
                create o "o1" assign to ["oa3", "oa6"]
                """;
        testCtx.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        List<String> conts = testCtx.graphReviewer().getAttributeContainers("o1");
        List<String> expected = List.of("oa3", "oa2", "oa1", "oa6", "oa5");
        assertTrue(conts.containsAll(expected));
        assertTrue(expected.containsAll(conts));
    }

    @Test
    void testGetPolicyClassContainers() throws PMException {
        TestContext testCtx = initTest();

        String pml = """
                      set resource access rights ["read", "write"]
                      create pc "pc1" {
                          oas {
                              "oa1"
                                  "oa2"
                                      "oa3"
                              "oa4"                                  
                          }
                      }
                                      
                      create pc "pc2" {
                          oas {
                              "oa5"
                                  "oa6"
                          }
                      }
                                      
                      create pc "pc3"
                                      
                      create o "o1" assign to ["oa3", "oa6"]
                      """;
        testCtx.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        List<String> pcs = testCtx.graphReviewer().getPolicyClassContainers("o1");
        List<String> expected = List.of("pc1", "pc2");
        assertTrue(pcs.containsAll(expected));
        assertTrue(expected.containsAll(pcs));
    }

    @Test
    void testIsContained() throws PMException {
        TestContext testCtx = initTest();
        String pml = """
                      set resource access rights ["read", "write"]
                      create pc "pc1" {
                          oas {
                              "oa1"
                                  "oa2"
                                      "oa3"
                              "oa4"                                  
                          }
                      }
                                      
                      create pc "pc2" {
                          oas {
                              "oa5"
                                  "oa6"
                          }
                      }
                                      
                      create pc "pc3"
                                      
                      create o "o1" assign to ["oa3", "oa6"]
                      """;
        testCtx.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        assertTrue(testCtx.graphReviewer().isContained("o1", "oa1"));
        assertTrue(testCtx.graphReviewer().isContained("o1", "oa2"));
        assertTrue(testCtx.graphReviewer().isContained("o1", "oa3"));
        assertTrue(testCtx.graphReviewer().isContained("o1", "pc1"));
        assertTrue(testCtx.graphReviewer().isContained("o1", "pc2"));
        assertFalse(testCtx.graphReviewer().isContained("o1", "pc3"));
    }
}
