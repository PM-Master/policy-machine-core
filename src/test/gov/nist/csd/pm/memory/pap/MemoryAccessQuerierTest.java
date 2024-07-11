package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.AccessQuerierTest;
import gov.nist.csd.pm.common.exception.PMException;

class MemoryAccessQuerierTest extends AccessQuerierTest {

    @Override
    public TestContext initTest() throws PMException {
        PAP pap = new MemoryPAP();
        return new TestContext(pap);
    }
}