package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.graph.nodes.Node;
import gov.nist.csd.pm.pap.PolicyReview;

import java.util.Arrays;

import static gov.nist.csd.pm.common.graph.nodes.NodeType.PC;

public class PrivilegeChecker {

    private final PAP pap;
    private final PolicyReview policyReview;

    public PrivilegeChecker(PAP pap, PolicyReview policyReview) {
        this.pap = pap;
        this.policyReview = policyReview;
    }

    public void check(UserContext userCtx, String target, String... toCheck) throws PMException {
        // if checking the permissions on a PC, check the permissions on the target node for the PC
        Node targetNode = pap.graph().getNode(target);

        if (targetNode.getType().equals(PC)) {
            target = AdminPolicy.policyClassTargetName(target);
        }

        AccessRightSet accessRights = policyReview.access().computePrivileges(userCtx, target);
        if (!accessRights.containsAll(Arrays.asList(toCheck))) {
            throw new UnauthorizedException(userCtx, target, toCheck);
        }
    }
}
