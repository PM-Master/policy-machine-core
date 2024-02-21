package gov.nist.csd.pm.impl.memory;

import gov.nist.csd.pm.policy.exceptions.PMException;

public interface BaseMemoryTx {
    void rollback() throws PMException;
}
