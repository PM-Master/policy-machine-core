package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.Operation;

import java.util.Objects;

public class DissociateOp extends GraphOp {
    private final String ua;
    private final String target;

    public DissociateOp(String ua, String target) {
        this.ua = ua;
        this.target = target;
    }

    @Override
    public String getOpName() {
        return "dissociate";
    }

    @Override
    public Object[] getOperands() {
        return operands(ua, target);
    }

    public String getUa() {
        return ua;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (DissociateOp) obj;
        return Objects.equals(this.ua, that.ua) &&
                Objects.equals(this.target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ua, target);
    }

    @Override
    public String toString() {
        return "DissociateOp[" +
                "ua=" + ua + ", " +
                "target=" + target + ']';
    }


}
