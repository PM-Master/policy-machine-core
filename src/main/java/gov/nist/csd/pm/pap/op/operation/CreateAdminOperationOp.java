package gov.nist.csd.pm.pap.op.operation;

import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.*;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_ADMIN_OPERATION;

public class CreateAdminOperationOp extends Operation<Void> {

    public static final String OPERATION_OPERAND = "operation";

    public CreateAdminOperationOp() {
        super(
                "create_admin_operation",
                Map.of(OPERATION_OPERAND, new RequiredCapability()),
                (pap, user, op, capMap, operands) -> {
                    PrivilegeChecker.check(pap, user, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), CREATE_ADMIN_OPERATION);
                },
                (pap, operands) -> {
                    Operation<?> operation = (Operation<?>) operands.get(OPERATION_OPERAND);

                    pap.modify().operations().createAdminOperation(operation);

                    return null;
                }
        );
    }
}
