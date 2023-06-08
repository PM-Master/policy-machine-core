package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.pml.type.Type;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class FormalArgument implements Serializable {

    private String name;
    private Type type;

    public FormalArgument(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FormalArgument that = (FormalArgument) o;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "FormalArgument[" +
                "name=" + name + ", " +
                "type=" + type + ']';
    }

}
