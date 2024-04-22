package gov.nist.csd.pm.common.graph.relationships;

import gov.nist.csd.pm.common.exception.PMException;

public class InvalidAssociationException extends PMException {
    public InvalidAssociationException(String msg) {
        super(msg);
    }
}
