package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;

public class CreateObligationOp extends ObligationOp {

    public CreateObligationOp() {
        super("create_obligation", CREATE_OBLIGATION, (pap, operands) -> {
            pap.modify().obligations().create(
                    (UserContext) operands.get(0),
                    (String) operands.get(1),
                    (Collection<Rule>) operands.get(2)
            );

            return null;
        });
    }
}
