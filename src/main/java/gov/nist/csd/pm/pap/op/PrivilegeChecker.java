package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.pattern.Pattern;
import gov.nist.csd.pm.common.obligation.pattern.ReferencedNodes;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.graph.node.Node;

import java.util.Arrays;

import static gov.nist.csd.pm.common.graph.node.NodeType.PC;

public class PrivilegeChecker {

    public static void check(PAP pap, UserContext userCtx, String target, String... toCheck) throws PMException {
        // if checking the permissions on a PC, check the permissions on the target node for the PC
        Node targetNode = pap.query().graph().getNode(target);

        if (targetNode.getType().equals(PC)) {
            target = AdminPolicy.policyClassTargetName(target);
        }

        AccessRightSet accessRights = pap.query().access().computePrivileges(userCtx, target);
        if (!accessRights.containsAll(Arrays.asList(toCheck))) {
            throw new UnauthorizedException(userCtx, target, toCheck);
        }
    }

    public static void checkPattern(PAP pap, UserContext userCtx, Pattern pattern, String toCheck) throws PMException {
        ReferencedNodes referencedNodes = pattern.getReferencedNodes();
        if (referencedNodes.isAny()) {
            PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY.nodeName(), toCheck);

            return;
        }

        for (String entity : referencedNodes.nodes()) {
            // can only check privileges if the entity is a node
            if (!pap.query().graph().nodeExists(entity)) {
                continue;
            }

            PrivilegeChecker.check(pap, userCtx, entity, toCheck);
        }

    }
}
