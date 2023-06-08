package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.exception.PMLConstantAlreadyDefinedException;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.common.tx.Transactional;

import java.util.Collection;
import java.util.HashMap;

import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.common.graph.node.NodeType.PC;

/**
 * PolicyStore is an abstract class that outlines the expected behavior of a backend implementation.
 */
public abstract class PolicyModifier extends Modifier implements PolicyModification, Transactional, AdminPolicy.Verifier {

    @Override
    public abstract GraphModifier graph();

    @Override
    public abstract ProhibitionsModifier prohibitions();

    @Override
    public abstract ObligationsModifier obligations();

    @Override
    public abstract PMLModifier pml();

    @Override
    public void verifyAdminPolicyClassNode() throws PMException {
        if (!query().graph().nodeExists(AdminPolicyNode.ADMIN_POLICY.nodeName())) {
            graph().createNodeInternal(AdminPolicyNode.ADMIN_POLICY.nodeName(), PC, new HashMap<>());
        }
    }

    @Override
    public void verifyAdminPolicyAttribute(AdminPolicyNode node, AdminPolicyNode parent) throws PMException {
        if (!query().graph().nodeExists(node.nodeName())) {
            graph().createNodeInternal(node.nodeName(), OA, new HashMap<>());
        }

        if (!query().graph().getParents(node.nodeName()).contains(parent.nodeName())) {
            graph().createAssignmentInternal(node.nodeName(), parent.nodeName());
        }
    }

    @Override
    public void verifyAdminPolicyConstant(AdminPolicyNode constant) throws PMException {
        try {
            pml().createConstant(constant.constantName(), new StringValue(constant.nodeName()));
        } catch (PMLConstantAlreadyDefinedException e) {
            // ignore this exception as the admin policy constant already existing is not an error
        }
    }

    @Override
    public void verifyPolicyClassTargets() throws PMException {
        Collection<String> policyClasses = query().graph().getPolicyClasses();
        for (String pc : policyClasses) {
            // admin policy node are already handled
            if (AdminPolicy.isAdminPolicyNodeName(pc)) {
                continue;
            }

            String target = AdminPolicy.policyClassTargetName(pc);
            if (!query().graph().nodeExists(target)) {
                graph().createNodeInternal(target, OA, new HashMap<>());
            }

            if (!query().graph().getParents(target).contains(pc)) {
                graph().createAssignmentInternal(target, pc);
            }
        }
    }
}
