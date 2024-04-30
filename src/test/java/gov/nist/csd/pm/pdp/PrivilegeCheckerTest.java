package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.pdp.adjudicator.PrivilegeChecker;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrivilegeCheckerTest {

    PAP pap;

    @BeforeEach
    void setup() throws PMException {
        MemoryPolicyStore ps = new MemoryPolicyStore();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        pap = new PAP(ps, pr);

        pap.policy().graph().setResourceAccessRights(new AccessRightSet("read"));

        pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
        pap.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

        pap.policy().graph().associate("ua1", "oa1", new AccessRightSet("read"));
        pap.policy().graph().associate("ua1", AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), new AccessRightSet(AdminAccessRights.ASSIGN_TO));

        pap.policy().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
        pap.policy().graph().createObject("o1", new HashMap<>(), List.of("oa1"));
    }

    @Test
    void testAccessRightChecker() throws PMException {
        PrivilegeChecker privilegeChecker = new PrivilegeChecker(pap);
        privilegeChecker.check(new UserContext("u1"), "o1", "read");
        privilegeChecker.check(new UserContext("u1"), "pc1", AdminAccessRights.ASSIGN_TO);
        assertThrows(UnauthorizedException.class, () -> privilegeChecker.check(new UserContext("u1"), "pc1", AdminAccessRights.DELETE_POLICY_CLASS));
    }

}