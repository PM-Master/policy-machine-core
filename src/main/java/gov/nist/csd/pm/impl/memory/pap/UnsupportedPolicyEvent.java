package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.op.Operation;

public class UnsupportedPolicyEvent extends PMException {
    public UnsupportedPolicyEvent(Operation event) {
        super("policy event \"" + event.getOpName() + "\" is not supported by in memory transactions");
    }
}
