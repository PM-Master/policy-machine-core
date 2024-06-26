package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.operand.PolicyElementOperand;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN_TO;

public class AssignOp extends GraphOp {

    private final String ascendant;
    private final String descendant;

    public AssignOp(String ascendant, String descendant) {
        super("assign",
              new PolicyElementOperand("ascendant", ascendant, ASSIGN),
              new PolicyElementOperand("descendant", descendant, ASSIGN_TO));

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
        pap.modify().graph().assign(ascendant, descendant);
    }

    @Override
    public void canExecute(PAP pap, UserContext user) throws PMException {
        // check asc
        checkPrivilegesOnOperand(pap, user, (PolicyElementOperand) operands.get(0));

        // check desc
        checkPrivilegesOnOperand(pap, user, (PolicyElementOperand) operands.get(1));
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
