package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;

public class CreateObligationOp extends ObligationOp {

    public CreateObligationOp(UserContext author, String name, Collection<Rule> rules) {
        super("create_obligation", author, name, rules, CREATE_OBLIGATION);
    }

    public CreateObligationOp() {
        super("create_obligation", CREATE_OBLIGATION);
    }

    @Override
    public Void execute(PAP pap) throws PMException {
        pap.modify().obligations().create(author, name, rules);

        return null;
    }
}
