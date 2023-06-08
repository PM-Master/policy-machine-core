package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pdp.adjudicator.PrivilegeChecker;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrivilegeCheckerTest {

    @Test
    void testAccessRightChecker() throws PMException {
        PAP pap = new MemoryPAP();

        pap.modify().graph().setResourceAccessRights(new AccessRightSet("read"));

        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));

        pap.modify().graph().associate("ua1", "oa1", new AccessRightSet("read"));
        pap.modify().graph().associate("ua1", AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), new AccessRightSet(
                AdminAccessRights.ASSIGN_TO));

        pap.modify().graph().createUser("u1", new HashMap<>(), List.of("ua1"));
        pap.modify().graph().createObject("o1", new HashMap<>(), List.of("oa1"));

        PrivilegeChecker.check(pap, new UserContext("u1"), "o1", "read");
        PrivilegeChecker.check(pap, new UserContext("u1"), "pc1", AdminAccessRights.ASSIGN_TO);
        assertThrows(UnauthorizedException.class, () -> PrivilegeChecker.check(pap, new UserContext("u1"), "pc1", AdminAccessRights.DELETE_POLICY_CLASS));
    }

}