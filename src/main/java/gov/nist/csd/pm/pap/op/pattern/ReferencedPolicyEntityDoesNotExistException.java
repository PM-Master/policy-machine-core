package gov.nist.csd.pm.pap.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;

public class ReferencedPolicyEntityDoesNotExistException extends PMException {
    public ReferencedPolicyEntityDoesNotExistException(String entity) {
        super(entity + " is not a known policy entity");
    }
}
