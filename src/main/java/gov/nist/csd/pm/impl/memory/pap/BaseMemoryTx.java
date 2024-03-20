package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.policy.exceptions.PMException;

public interface BaseMemoryTx {
    void rollback() throws PMException;
}
