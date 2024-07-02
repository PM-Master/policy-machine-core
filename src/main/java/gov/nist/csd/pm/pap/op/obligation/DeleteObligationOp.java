package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_OBLIGATION;

public class DeleteObligationOp extends ObligationOp {

    public DeleteObligationOp(UserContext author, String name, Collection<Rule> rules) {
        super("delete_obligation", author, name, rules, DELETE_OBLIGATION);
    }

    public DeleteObligationOp() {
        super("delete_obligation", DELETE_OBLIGATION);
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().obligations().delete(name);
    }
}
