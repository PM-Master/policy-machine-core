package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.common.exception.PMException;

public class UnknownOperandException extends PMException {
    public UnknownOperandException(String operand) {
        super("unknown operand " + operand);
    }
}
