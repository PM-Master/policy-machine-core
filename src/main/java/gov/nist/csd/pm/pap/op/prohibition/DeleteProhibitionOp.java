package gov.nist.csd.pm.pap.op.prohibition;

import java.util.Objects;

public class DeleteProhibitionOp extends ProhibitionsOp {
    private final String name;

    public DeleteProhibitionOp(String name) {
        super(operands(name));
        this.name = name;
    }

    @Override
    public String getOpName() {
        return "delete_prohibition";
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
        var that = (DeleteProhibitionOp) obj;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "DeleteProhibitionOp[" +
                "name=" + name + ']';
    }


}
