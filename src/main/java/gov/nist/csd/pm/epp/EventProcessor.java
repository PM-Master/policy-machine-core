package gov.nist.csd.pm.epp;

import gov.nist.csd.pm.common.exception.PMException;

public interface EventProcessor {

    void processEvent(EventContext eventCtx) throws PMException;

}
