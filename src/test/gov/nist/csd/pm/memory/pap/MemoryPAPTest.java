package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PAPTest;

import static org.junit.jupiter.api.Assertions.*;

class MemoryPAPTest extends PAPTest {

    @Override
    public PAP getPAP() throws PMException {
        return new MemoryPAP();
    }
}