package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class DeassignOp extends GraphOp {

    private String ascendant;
    private String descendant;

    public DeassignOp(String opName, List<RequiredCapability> capMap) {
        super("deassign",
              List.of(
                      new RequiredCapability(DEASSIGN),
                      new RequiredCapability(DEASSIGN_FROM)
              )
        );
    }

    public DeassignOp(String ascendant, String descendant) {
        super("deassign",
              List.of(
                      new RequiredCapability(DEASSIGN),
                      new RequiredCapability(DEASSIGN_FROM)
              )
        );

        setOperands(ascendant, descendant);
    }

    @Override
    public void setOperands(List<Object> operands) {
        super.setOperands(operands);

        ascendant = (String) operands.get(0);
        descendant = (String) operands.get(1);
    }

    public String getAscendant() {
        return ascendant;
    }

    public String getDescendant() {
        return descendant;
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().deassign(ascendant, descendant);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (DeassignOp) obj;
        return Objects.equals(this.ascendant, that.ascendant) &&
                Objects.equals(this.descendant, that.descendant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ascendant, descendant);
    }

    @Override
    public String toString() {
        return "DeassignOp[" +
                "ascendant=" + ascendant + ", " +
                "descendant=" + descendant + ']';
    }
}
