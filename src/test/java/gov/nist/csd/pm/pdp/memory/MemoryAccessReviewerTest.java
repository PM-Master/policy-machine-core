package gov.nist.csd.pm.pdp.memory;

import gov.nist.csd.pm.impl.memory.pdp.MemoryAccessReviewer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.pdp.AccessReviewerTest;
import gov.nist.csd.pm.common.exception.PMException;

class MemoryAccessReviewerTest extends AccessReviewerTest {

    @Override
    public TestContext initTest() throws PMException {
        PAP pap = new PAP(new MemoryPolicyStore());
        return new TestContext(new MemoryAccessReviewer(pap), pap);
    }
}