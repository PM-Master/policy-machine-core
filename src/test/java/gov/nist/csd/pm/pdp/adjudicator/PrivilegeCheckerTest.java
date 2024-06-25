package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.pap.exception.NodeDoesNotExistException;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrivilegeCheckerTest {

    static PAP pap;

    @BeforeAll
    static void setup() throws PMException {
        pap = new MemoryPAP();
        pap.deserialize(
                new UserContext("u1"),
                List.of("""
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
                        """),
                        new PMLDeserializer()
        );
    }

    @Test
    void testCheckUserAndTargetDoesNotExist() throws PMException {
        assertThrows(NodeDoesNotExistException.class,
                     () -> PrivilegeChecker.check(pap, new UserContext("u3"), "o1", "read"));
        assertThrows(NodeDoesNotExistException.class,
                     () -> PrivilegeChecker.check(pap, new UserContext("u1"), "o2", "read"));
    }

    @Test
    void testCheckNodeIsPC() {
        assertDoesNotThrow(() -> PrivilegeChecker.check(pap, new UserContext("u1"), "pc1", "read"));
    }

    @Test
    void testAuthorize() {
        assertDoesNotThrow(() -> PrivilegeChecker.check(pap, new UserContext("u1"), "o1", "read"));
    }

    @Test
    void testUnauthorized() {
        assertThrows(PMException.class,
                     () -> PrivilegeChecker.check(pap, new UserContext("u2"), "o1", "read"));
    }

    @Test
    void testEmptyAccessRights() {
        assertDoesNotThrow(() -> PrivilegeChecker.check(pap, new UserContext("u1"), "o1"));
    }

}