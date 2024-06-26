package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.operand.PolicyElementOperand;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class DeassignOp extends GraphOp {

    private final String ascendant;
    private final String descendant;

    public DeassignOp(String ascendant, String descendant) {
        super("deassign",
              new PolicyElementOperand("ascendant", ascendant, DEASSIGN),
              new PolicyElementOperand("descendant", descendant, DEASSIGN_FROM)
        );

        this.ascendant = ascendant;
        this.descendant = descendant;
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
    public void canExecute(PAP pap, UserContext userCtx) throws PMException {
        // check asc
        checkPrivilegesOnOperand(pap, userCtx, (PolicyElementOperand) operands.get(0));

        // check desc
        checkPrivilegesOnOperand(pap, userCtx, (PolicyElementOperand) operands.get(1));
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
