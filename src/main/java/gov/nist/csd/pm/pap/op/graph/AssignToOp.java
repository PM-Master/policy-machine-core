package gov.nist.csd.pm.pap.op.graph;

import java.util.Objects;

public class AssignToOp extends GraphOp {
    private final String child;
    private final String parent;

    public AssignToOp(String child, String parent) {
        this.child = child;
        this.parent = parent;
    }

    @Override
    public String getOpName() {
        return "assign_to";
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
        var that = (AssignToOp) obj;
        return Objects.equals(this.child, that.child) &&
                Objects.equals(this.parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(child, parent);
    }

    @Override
    public String toString() {
        return "AssignToOp[" +
                "child=" + child + ", " +
                "parent=" + parent + ']';
    }


}
