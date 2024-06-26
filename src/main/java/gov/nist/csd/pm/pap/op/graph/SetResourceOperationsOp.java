package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.SET_RESOURCE_ACCESS_RIGHTS;

public class SetResourceOperationsOp extends GraphOp {

    private final AccessRightSet operations;

    public SetResourceOperationsOp(AccessRightSet operations) {
        super("set_resource_access_rights",
              new Operand("operations", operations));
        this.operations = operations;
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().setResourceAccessRights(operations);
    }

    @Override
    public void canExecute(PAP pap, UserContext userCtx) throws PMException {
        checkPrivilegesOnAdminNode(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET, SET_RESOURCE_ACCESS_RIGHTS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SetResourceOperationsOp that = (SetResourceOperationsOp) o;
        return Objects.equals(operations, that.operations);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(operations);
    }

    @Override
    public String toString() {
        return "SetResourceOperationsOp{" +
                "operations=" + operations +
                '}';
    }
}
