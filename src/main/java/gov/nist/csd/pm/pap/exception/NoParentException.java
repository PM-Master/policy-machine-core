package gov.nist.csd.pm.pap.exception;

import gov.nist.csd.pm.common.exception.PMException;

public class NoParentException extends PMException {
    public NoParentException() {
        super("a null or empty parent value was provided");
    }
}
