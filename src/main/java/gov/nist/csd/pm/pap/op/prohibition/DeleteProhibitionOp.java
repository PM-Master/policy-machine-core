package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.pap.exception.PMException;
import gov.nist.csd.pm.pap.PAP;

import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class DeleteProhibitionOp extends ProhibitionOp {

    public DeleteProhibitionOp() {
        super("delete_prohibition", DELETE_PROCESS_PROHIBITION, DELETE_PROHIBITION);
    }

    @Override
    public Void execute(PAP pap, Map<String, Object> operands) throws PMException {
        pap.modify().prohibitions().deleteProhibition(
                (String) operands.get(NAME_OPERAND)
        );

        return null;
    }
}
