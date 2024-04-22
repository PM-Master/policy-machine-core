package gov.nist.csd.pm.pdp.memory;

import gov.nist.csd.pm.impl.memory.pdp.MemoryGraphReviewer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.pdp.GraphReviewerTest;
import gov.nist.csd.pm.common.exception.PMException;

class MemoryGraphReviewerTest extends GraphReviewerTest {

    @Override
    public TestContext initTest() throws PMException {
        PAP pap = new PAP(new MemoryPolicyStore());
        return new TestContext(new MemoryGraphReviewer(pap), pap);
    }
}