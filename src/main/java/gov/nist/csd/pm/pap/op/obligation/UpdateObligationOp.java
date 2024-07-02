package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;

public class UpdateObligationOp extends ObligationOp {

    public UpdateObligationOp(UserContext author, String name, Collection<Rule> rules) {
        super("update_obligation", author, name, rules, CREATE_OBLIGATION);
    }

    public UpdateObligationOp() {
        super("update_obligation", CREATE_OBLIGATION);
    }

    @Override
    public Void execute(PAP pap) throws PMException {
        pap.modify().obligations().update(author, name, rules);
    }
}
