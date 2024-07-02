package gov.nist.csd.pm.pap.op.operation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.op.graph.GraphOp;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.SET_RESOURCE_OPERATIONS;

public class SetResourceOperationsOp extends GraphOp {

    private AccessRightSet operations;

    public SetResourceOperationsOp() {
        super("set_resource_operations",
              List.of(new RequiredCapability("operations")));
    }

    public SetResourceOperationsOp(AccessRightSet operations) {
        super("set_resource_operations",
              List.of(new RequiredCapability("operations")));

        setOperands(operands);
    }

    @Override
    public void setOperands(List<Object> operands) {
        super.setOperands(operands);

        this.operations = (AccessRightSet) operands.getFirst();
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().setResourceAccessRights(operations);
    }

    @Override
    public Operation canExecute(PAP pap, UserContext userCtx) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), SET_RESOURCE_OPERATIONS);

        return this;
    }
}
