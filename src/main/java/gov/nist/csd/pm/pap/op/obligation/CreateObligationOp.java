package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.pap.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.obligation.Rule;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBLIGATION;

public class CreateObligationOp extends ObligationOp {

    public CreateObligationOp() {
        super("create_obligation", CREATE_OBLIGATION);
    }

    @Override
    public Void execute(PAP pap, Map<String, Object> operands) throws PMException {
        pap.modify().obligations().createObligation(
                (String) operands.get(AUTHOR_OPERAND),
                (String) operands.get(NAME_OPERAND),
                (List<Rule>) operands.get(RULES_OPERAND)
        );

        return null;
    }
}
