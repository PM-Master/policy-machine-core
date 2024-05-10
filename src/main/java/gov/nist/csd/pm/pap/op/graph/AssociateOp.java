package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.Objects;

public class AssociateOp extends GraphOp {
    private final String ua;
    private final String target;
    private final AccessRightSet accessRightSet;

    public AssociateOp(String ua, String target, AccessRightSet accessRightSet) {
        this.ua = ua;
        this.target = target;
        this.accessRightSet = accessRightSet;
    }

    @Override
    public String getOpName() {
        return "associate";
    }

    @Override
    public Object[] getOperands() {
        return operands(ua, target, accessRightSet);
    }

    public String ua() {
        return ua;
    }

    public String target() {
        return target;
    }

    public AccessRightSet accessRightSet() {
        return accessRightSet;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (AssociateOp) obj;
        return Objects.equals(this.ua, that.ua) &&
                Objects.equals(this.target, that.target) &&
                Objects.equals(this.accessRightSet, that.accessRightSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ua, target, accessRightSet);
    }

    @Override
    public String toString() {
        return "AssociateOp[" +
                "ua=" + ua + ", " +
                "target=" + target + ", " +
                "accessRightSet=" + accessRightSet + ']';
    }


}
