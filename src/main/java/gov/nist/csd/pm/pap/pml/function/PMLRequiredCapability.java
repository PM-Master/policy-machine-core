package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.List;
import java.util.Objects;

public class PMLRequiredCapability extends RequiredCapability {

    protected final Type type;

    public PMLRequiredCapability(String operand, Type type, List<String> caps) {
        super(operand, caps);
        this.type = type;
    }

    public PMLRequiredCapability(String operand, Type type) {
        super(operand);
        this.type = type;
    }

    public Type type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PMLRequiredCapability that = (PMLRequiredCapability) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    @Override
    public String toString() {
        return "PMLRequiredCapability{" +
                "type=" + type +
                '}';
    }
}
