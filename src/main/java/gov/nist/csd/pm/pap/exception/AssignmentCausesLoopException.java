package gov.nist.csd.pm.pap.exception;

import gov.nist.csd.pm.common.exception.PMException;

public class AssignmentCausesLoopException extends PMException {

    public AssignmentCausesLoopException(String child, String parent) {
        super("a relation between " + child + " and " + parent + " would cause a loop in the graph");
    }

}
