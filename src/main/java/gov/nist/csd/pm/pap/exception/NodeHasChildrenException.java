package gov.nist.csd.pm.pap.exception;

import gov.nist.csd.pm.common.exception.PMException;

public class NodeHasChildrenException extends PMException {
    public NodeHasChildrenException(String node) {
        super("cannot delete " + node + ", it has nodes assigned to it");
    }
}
