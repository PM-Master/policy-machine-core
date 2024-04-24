package gov.nist.csd.pm.common.op.graph;

import gov.nist.csd.pm.common.op.Operation;
import gov.nist.csd.pm.pdp.AccessRightSet;

import java.io.Serial;
import java.util.Objects;

public class SetResourceAccessRightsOp implements Operation {
    @Serial
    private static final long serialVersionUID = 0L;
    private final AccessRightSet accessRightSet;

    public SetResourceAccessRightsOp(AccessRightSet accessRightSet) {
        this.accessRightSet = accessRightSet;
    }

    @Override
    public String getOpName() {
        return "set_resource_access_rights";
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

    public AccessRightSet accessRightSet() {
        return accessRightSet;
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
