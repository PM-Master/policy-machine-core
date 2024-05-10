package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Objects;

public class PatternVarValue extends Value {

    private String varName;

    public PatternVarValue(String varName) {
        super(Type.any());
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PatternVarValue that = (PatternVarValue) o;
        return Objects.equals(varName, that.varName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(varName);
    }

    @Override
    public String toString() {
        return varName;
    }
}
