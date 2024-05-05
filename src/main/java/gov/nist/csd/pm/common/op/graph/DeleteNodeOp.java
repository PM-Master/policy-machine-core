package gov.nist.csd.pm.common.op.graph;

import gov.nist.csd.pm.common.op.Operation;

import java.io.Serial;
import java.util.Objects;

public class DeleteNodeOp extends GraphOp {
    private final String name;

    public DeleteNodeOp(String name) {
        super(operands(name));
        this.name = name;
    }

    @Override
    public String getOpName() {
        return "delete_node";
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
