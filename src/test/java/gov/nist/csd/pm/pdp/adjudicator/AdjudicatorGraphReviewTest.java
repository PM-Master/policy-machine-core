package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.memory.MemoryPolicyStore;
import gov.nist.csd.pm.pap.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.pdp.reviewer.PolicyReviewer;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.UserContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjudicatorGraphReviewTest {

    static AdjudicatorGraphReview u1;
    static AdjudicatorGraphReview u2;

    @BeforeAll
    static void setup() throws PMException {
        PAP pap = new PAP(new MemoryPolicyStore());
        pap.deserialize(
                new UserContext("u1"),
                """
                set resource access rights ["read", "write"]
                
                create policy class "pc1" {
                    uas {
                        "ua1"
                        "ua2"
                    }
                    oas {
                        "oa1"
                            "oa1-1"
                            "oa1-2"
                    }
                    associations {
                        "ua1" and "ua2" with [review_policy]
                        "ua1" and "oa1" with [review_policy]
                    }
                }
                
                create user "u1" assign to ["ua1", "ua2"]
                create user "u2" assign to ["ua2"]    
                
                create o "o1" assign to ["oa1-1", "oa1-2"]            
                """,
                new PMLDeserializer()
        );

        u1 = new AdjudicatorGraphReview(new UserContext("u1"), new PrivilegeChecker(pap, new PolicyReviewer(pap)));
        u2 = new AdjudicatorGraphReview(new UserContext("u2"), new PrivilegeChecker(pap, new PolicyReviewer(pap)));
    }

    @Test
    void testGetAttributeContainers() {
        assertThrows(PMException.class, () -> {
            u2.getAttributeContainers("o1");
        });
        assertDoesNotThrow(() -> {
            u1.getAttributeContainers("o1");
        });
    }

    @Test
    void testGetPolicyClassContainers() {
        assertThrows(PMException.class, () -> {
            u2.getPolicyClassContainers("o1");
        });
        assertDoesNotThrow(() -> {
            u1.getPolicyClassContainers("o1");
        });
    }

    @Test
    void testIsContained() {
        assertThrows(PMException.class, () -> {
            u2.isContained("o1", "oa1");
        });
        assertDoesNotThrow(() -> {
            u1.isContained("o1", "oa1");
        });
    }

}