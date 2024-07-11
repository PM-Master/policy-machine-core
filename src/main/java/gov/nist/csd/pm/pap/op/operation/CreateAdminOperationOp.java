package gov.nist.csd.pm.pap.op.operation;

import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.*;

import java.util.List;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_ADMIN_OPERATION;

public class CreateAdminOperationOp extends Operation<Void> {
    public CreateAdminOperationOp() {
        super(
                "create_admin_operation",
                List.of(new RequiredCapability("operation")),
                (pap, user, op, capMap, operands) -> {
                    PrivilegeChecker.check(pap, user, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), CREATE_ADMIN_OPERATION);
                },
                (pap, operands) -> {
                    Operation<?> operation = (Operation<?>) operands.get(0);

                    pap.modify().operations().createAdminOperation(operation);

                    return null;
                });
    }
}
