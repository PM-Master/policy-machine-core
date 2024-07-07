package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_OBLIGATION;

public class DeleteObligationOp extends ObligationOp {

    public DeleteObligationOp() {
        super("delete_obligation", DELETE_OBLIGATION, (pap, operands) -> {
            pap.modify().obligations().delete((String) operands.get(1));

            return null;
        });
    }
}
