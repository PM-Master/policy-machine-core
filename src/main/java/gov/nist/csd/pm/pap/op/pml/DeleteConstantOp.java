package gov.nist.csd.pm.pap.op.pml;

import java.util.Objects;

public class DeleteConstantOp extends PMLOp {
    private final String name;

    public DeleteConstantOp(String name) {
        this.name = name;
    }

    @Override
    public String getOpName() {
        return "delete_constant";
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
        var that = (DeleteConstantOp) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "DeleteConstantOp[" +
                "name=" + name + ']';
    }


}
