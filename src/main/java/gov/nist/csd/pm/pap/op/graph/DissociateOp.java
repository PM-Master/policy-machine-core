package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.operand.PolicyElementOperand;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSOCIATE_TO;

public class DissociateOp extends GraphOp {
    private final String ua;
    private final String target;

    public DissociateOp(String ua, String target) {
        super("dissociate",
              new PolicyElementOperand("ua", ua, ASSOCIATE),
              new PolicyElementOperand("target", target, ASSOCIATE_TO));

        this.ua = ua;
        this.target = target;
    }

    public String getUa() {
        return ua;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String getOpName() {
        return "associate";
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().dissociate(ua, target);
    }

    @Override
    public void canExecute(PAP pap, UserContext userCtx) throws PMException {
        checkPrivilegesOnOperand(pap, userCtx, (PolicyElementOperand) operands.get(0));
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
        var that = (DissociateOp) obj;
        return Objects.equals(this.ua, that.ua) &&
                Objects.equals(this.target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ua, target);
    }

    @Override
    public String toString() {
        return "AssociateOp[" +
                "ua=" + ua + ", " +
                "target=" + target + ']';
    }
}
