package gov.nist.csd.pm.pap.op.graph;

import java.util.Objects;

public class DeleteNodeOp extends GraphOp {
    private final String name;

    public DeleteNodeOp(String name) {
        this.name = name;
    }

    @Override
    public String getOpName() {
        return "delete_node";
    }

    @Override
    public Object[] getOperands() {
        return operands(name);
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (DeleteNodeOp) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "DeleteNodeOp[" +
                "name=" + name + ']';
    }


}
