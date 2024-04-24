package gov.nist.csd.pm.common.op.graph;

import gov.nist.csd.pm.common.op.Operation;

import java.io.Serial;
import java.util.Objects;

public class AssignToOp implements Operation {
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
