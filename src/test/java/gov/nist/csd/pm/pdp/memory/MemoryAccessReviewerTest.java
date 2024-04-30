package gov.nist.csd.pm.pdp.memory;

import gov.nist.csd.pm.impl.memory.pdp.MemoryAccessReviewer;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.pdp.AccessReviewerTest;
import gov.nist.csd.pm.common.exception.PMException;

class MemoryAccessReviewerTest extends AccessReviewerTest {

    @Override
    public TestContext initTest() throws PMException {
        MemoryPolicyStore ps = new MemoryPolicyStore();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        return new TestContext(new MemoryAccessReviewer(pap.policy()), pap.policy());
    }
}