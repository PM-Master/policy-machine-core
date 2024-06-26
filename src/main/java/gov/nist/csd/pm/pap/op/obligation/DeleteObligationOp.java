package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.query.UserContext;

import javax.ws.rs.DELETE;
import java.util.Collection;
import java.util.List;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_OBLIGATION;

public class DeleteObligationOp extends ObligationOp {

    public DeleteObligationOp(UserContext author, String name, Collection<Rule> rules) {
        super("delete_obligation", author, name, rules, DELETE_OBLIGATION);
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().obligations().delete(name);
    }

    @Override
    public String toString() {
        return "DeleteObligationOp{" +
                "author=" + author +
                ", reqCap='" + reqCap + '\'' +
                ", rules=" + rules +
                ", name='" + name + '\'' +
                '}';
    }
}
