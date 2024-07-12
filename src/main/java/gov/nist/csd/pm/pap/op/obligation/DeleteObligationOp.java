package gov.nist.csd.pm.pap.op.obligation;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_OBLIGATION;

public class DeleteObligationOp extends ObligationOp {

    public DeleteObligationOp() {
        super("delete_obligation", DELETE_OBLIGATION, (pap, operands) -> {
            pap.modify().obligations().delete((String) operands.get(NAME_OPERAND));

            return null;
        });
    }
}
