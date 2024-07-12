package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.List;
import java.util.Objects;

public class PMLRequiredCapability extends RequiredCapability {

    private final int order;
    private final Type type;

    public PMLRequiredCapability(int order, Type type, List<String> caps) {
        super(caps);
        this.order = order;
        this.type = type;
    }

    public PMLRequiredCapability(int order, Type type) {
        super();
        this.order = order;
        this.type = type;
    }

    public int order() {
        return order;
    }

    public Type type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PMLRequiredCapability that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return order == that.order && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), order, type);
    }

    @Override
    public String toString() {
        return "PMLOperationRequiredCapability{" +
                "order=" + order +
                ", type=" + type +
                '}';
    }
}
