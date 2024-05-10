package gov.nist.csd.pm.epp;

import gov.nist.csd.pm.common.exception.PMException;

public interface EventEmitter {

    void addEventListener(EventProcessor processor);
    void removeEventListener(EventProcessor processor);
    void emitEvent(EventContext event) throws PMException;

}
