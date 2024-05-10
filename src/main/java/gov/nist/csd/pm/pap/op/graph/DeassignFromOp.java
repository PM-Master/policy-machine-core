package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.Operation;

import java.util.Objects;

public class DeassignFromOp extends GraphOp {
    private final String child;
    private final String parent;

    public DeassignFromOp(String child, String parent) {
        this.child = child;
        this.parent = parent;
    }

    @Override
    public String getOpName() {
        return "deassign_from";
    }

    @Override
    public Object[] getOperands() {
        return operands(child, parent);
    }

    public String child() {
        return child;
    }

    public String parent() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (DeassignFromOp) obj;
        return Objects.equals(this.child, that.child) &&
                Objects.equals(this.parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(child, parent);
    }

    @Override
    public String toString() {
        return "DeassignFromOp[" +
                "child=" + child + ", " +
                "parent=" + parent + ']';
    }


}
