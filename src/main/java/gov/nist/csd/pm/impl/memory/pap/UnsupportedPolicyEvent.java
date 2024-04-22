package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.PolicyEvent;

public class UnsupportedPolicyEvent extends PMException {
    public UnsupportedPolicyEvent(PolicyEvent event) {
        super("policy event \"" + event.getEventName() + "\" is not supported by in memory transactions");
    }
}
