package gov.nist.csd.pm.common.op.userdefinedpml;

import gov.nist.csd.pm.pap.pml.value.Value;

import java.io.Serial;
import java.util.Objects;

public class CreateConstantOp implements UserDefinedPMLOp {
    @Serial
    private static final long serialVersionUID = 0L;
    private final String name;
    private final Value value;

    public CreateConstantOp(String name, Value value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getOpName() {
        return "create_constant";
    }

    public String name() {
        return name;
    }

    public Value value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (CreateConstantOp) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "CreateConstantOp[" +
                "name=" + name + ", " +
                "value=" + value + ']';
    }


}
