package gov.nist.csd.pm.pap.op;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequiredCapability {
    private final String operand;
    private final List<String> caps;

    public RequiredCapability(String operand, List<String> caps) {
        this.operand = operand;
        this.caps = caps;
    }

    public RequiredCapability(String operand) {
        this(operand, new ArrayList<>());
    }

    public String[] capsArray() {
        return caps.toArray(String[]::new);
    }

    public String operand() {
        return operand;
    }

    public List<String> caps() {
        return caps;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (RequiredCapability) obj;
        return Objects.equals(this.operand, that.operand) &&
                Objects.equals(this.caps, that.caps);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand, caps);
    }

    @Override
    public String toString() {
        return "RequiredCapability[" +
                "operand=" + operand + ", " +
                "caps=" + caps + ']';
    }


}
