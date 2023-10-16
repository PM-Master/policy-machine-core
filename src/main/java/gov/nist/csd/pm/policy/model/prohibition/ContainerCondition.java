package gov.nist.csd.pm.policy.model.prohibition;

import java.io.Serializable;
import java.util.Objects;

public final class ContainerCondition implements Serializable {

    private String name;
    private boolean complement;

    public ContainerCondition() {
    }

    public ContainerCondition(String name, boolean complement) {
        this.name = name;
        this.complement = complement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isComplement() {
        return complement;
    }

    public void setComplement(boolean complement) {
        this.complement = complement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContainerCondition that = (ContainerCondition) o;
        return complement == that.complement && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, complement);
    }

    @Override
    public String toString() {
        return "ContainerCondition[" +
                "name=" + name + ", " +
                "complement=" + complement + ']';
    }

}
