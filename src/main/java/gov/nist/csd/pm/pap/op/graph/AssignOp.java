package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.Operation;

import java.util.Objects;

public class AssignOp extends GraphOp {

    private final String child;
    private final String parent;

    public AssignOp(String child, String parent) {
        this.child = child;
        this.parent = parent;
    }

    @Override
    public String getOpName() {
        return "assign";
    }

    @Override
    public Object[] getOperands() {
        return operands(child, parent);
    }

    public String getChild() {
        return child;
    }

    public String getParent() {
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
        var that = (AssignOp) obj;
        return Objects.equals(this.child, that.child) &&
                Objects.equals(this.parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(child, parent);
    }

    @Override
    public String toString() {
        return "AssignOp[" +
                "child=" + child + ", " +
                "parent=" + parent + ']';
    }
}
