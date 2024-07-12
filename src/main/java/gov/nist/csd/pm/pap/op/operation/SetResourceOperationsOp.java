package gov.nist.csd.pm.pap.op.operation;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.SET_RESOURCE_OPERATIONS;

public class SetResourceOperationsOp extends Operation<Void> {

    public static final String OPERATION_OPERAND = "operation";

    public SetResourceOperationsOp() {
        super(
                "set_resource_operations",
                Map.of(OPERATION_OPERAND, new RequiredCapability()),
                (pap, userCtx, op, capMap, operands) -> {
                    PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), SET_RESOURCE_OPERATIONS);
                },
                (pap, operands) -> {
                    pap.modify().graph().setResourceAccessRights((AccessRightSet) operands.get(0));

                    return null;
                }
        );
    }
}
