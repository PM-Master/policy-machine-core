package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;

public interface BaseMemoryTx {
    void rollback() throws PMException;
}
