package gov.nist.csd.pm.epp;

import gov.nist.csd.pm.common.exception.PMException;

public interface EventEmitter {

    void addEventListener(EventProcessor listener);
    void removeEventListener(EventProcessor listener);
    void emitEvent(EventContext event) throws PMException;

}
