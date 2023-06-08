package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.Objects;

public class SetResourceAccessRightsOp extends GraphOp {
    private final AccessRightSet accessRightSet;

    public SetResourceAccessRightsOp(AccessRightSet accessRightSet) {
        this.accessRightSet = accessRightSet;
    }

    @Override
    public String getOpName() {
        return "set_resource_access_rights";
    }

    @Override
    public Object[] getOperands() {
        return operands();
    }

    public AccessRightSet getAccessRightSet() {
        return accessRightSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SetResourceAccessRightsOp that = (SetResourceAccessRightsOp) o;
        return Objects.equals(accessRightSet, that.accessRightSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessRightSet);
    }

    @Override
    public String toString() {
        return "SetResourceAccessRightsOp[" +
                "accessRightSet=" + accessRightSet + ']';
    }


}
