package gov.nist.csd.pm.pap.exception;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;

public class InvalidOperationException extends PMException {
    public InvalidOperationException(String op, List<RequiredCapability> caps, Object ... operands) {
        super("invalid input for operation " + op + ": expected " + caps.size() + " operands, got " + operands.length);
    }
}
