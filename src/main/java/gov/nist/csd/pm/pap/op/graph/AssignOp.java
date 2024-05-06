package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.Operation;

import java.util.Objects;

public class AssignOp extends GraphOp {

    private final String child;
    private final String parent;

    public AssignOp(String child, String parent) {
        super(Operation.operands(child, parent));
        this.child = child;
        this.parent = parent;
    }

    @Override
    public String getOpName() {
        return "assign";
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
