package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.graph.node.Node;

import java.util.Arrays;

import static gov.nist.csd.pm.common.graph.node.NodeType.PC;

public class PrivilegeChecker {

    private final PAP pap;

    public PrivilegeChecker(PAP pap) {
        this.pap = pap;
    }

    public void check(UserContext userCtx, String target, String... toCheck) throws PMException {
        // if checking the permissions on a PC, check the permissions on the target node for the PC
        Node targetNode = pap.policy().graph().getNode(target);

        if (targetNode.getType().equals(PC)) {
            target = AdminPolicy.policyClassTargetName(target);
        }

        AccessRightSet accessRights = pap.review().access().computePrivileges(userCtx, target);
        if (!accessRights.containsAll(Arrays.asList(toCheck))) {
            throw new UnauthorizedException(userCtx, target, toCheck);
        }
    }
}
