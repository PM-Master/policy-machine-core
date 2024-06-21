package gov.nist.csd.pm.pap.op.graph;

import java.util.Objects;

public class AssignOp extends GraphOp {

    private final String ascendant;
    private final String descendant;

    public AssignOp(String ascendant, String descendant) {
        this.ascendant = ascendant;
        this.descendant = descendant;
    }

    @Override
    public String getOpName() {
        return "assign";
    }

    @Override
    public Object[] getOperands() {
        return operands(ascendant, descendant);
    }

    public String getAscendant() {
        return ascendant;
    }

    public String getDescendant() {
        return descendant;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (AssignOp) obj;
        return Objects.equals(this.ascendant, that.ascendant) &&
                Objects.equals(this.descendant, that.descendant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ascendant, descendant);
    }

    @Override
    public String toString() {
        return "AssignOp[" +
                "ascendant=" + ascendant + ", " +
                "descendant=" + descendant + ']';
    }
}
