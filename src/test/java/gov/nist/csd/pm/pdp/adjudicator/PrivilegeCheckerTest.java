package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.memory.MemoryPolicyStore;
import gov.nist.csd.pm.pap.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.pdp.reviewer.PolicyReviewer;
import gov.nist.csd.pm.policy.exceptions.NodeDoesNotExistException;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.UserContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrivilegeCheckerTest {

    static PrivilegeChecker privilegeChecker;

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
                            }
                            associations {
                                "ua1" and "oa1" with ["read", "write"]
                                "ua1" and POLICY_CLASS_TARGETS with ["read"]
                            }
                        }
                        
                        create user "u1" assign to ["ua1"]
                        create user "u2" assign to ["ua2"]
                        
                        create object "o1" assign to ["oa1"]
                        """,
                        new PMLDeserializer()
        );
        privilegeChecker = new PrivilegeChecker(pap, new PolicyReviewer(pap));
    }

    @Test
    void testCheckUserAndTargetDoesNotExist() throws PMException {
        assertThrows(NodeDoesNotExistException.class,
                     () -> privilegeChecker.check(new UserContext("u3"), "o1", "read"));
        assertThrows(NodeDoesNotExistException.class,
                     () -> privilegeChecker.check(new UserContext("u1"), "o2", "read"));
    }

    @Test
    void testCheckNodeIsPC() {
        assertDoesNotThrow(() -> privilegeChecker.check(new UserContext("u1"), "pc1", "read"));
    }

    @Test
    void testAuthorize() {
        assertDoesNotThrow(() -> privilegeChecker.check(new UserContext("u1"), "o1", "read"));
    }

    @Test
    void testUnauthorized() {
        assertThrows(PMException.class,
                     () -> privilegeChecker.check(new UserContext("u2"), "o1", "read"));
    }

    @Test
    void testEmptyAccessRights() {
        assertDoesNotThrow(() -> privilegeChecker.check(new UserContext("u1"), "o1"));
    }

}