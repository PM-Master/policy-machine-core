package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.op.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;

public class CreateObligationOp extends ObligationOp {

    public CreateObligationOp(UserContext author, String name, Collection<Rule> rules) {
        super("create_obligation", author, name, rules, CREATE_OBLIGATION);
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().obligations().create(author, name, rules);
    }

    @Override
    public String toString() {
        return "CreateObligationOp{" +
                "author=" + author +
                ", name='" + name + '\'' +
                ", rules=" + rules +
                ", reqCap='" + reqCap + '\'' +
                '}';
    }
}
