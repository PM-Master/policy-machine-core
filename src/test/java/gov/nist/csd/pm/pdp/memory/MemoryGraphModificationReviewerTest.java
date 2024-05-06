package gov.nist.csd.pm.pdp.memory;

import gov.nist.csd.pm.impl.memory.pdp.MemoryGraphReviewer;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.pdp.GraphModificationReviewerTest;
import gov.nist.csd.pm.common.exception.PMException;

class MemoryGraphModificationReviewerTest extends GraphModificationReviewerTest {

    @Override
    public TestContext initTest() throws PMException {
        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        return new TestContext(new MemoryGraphReviewer(pap.policy()), pap.policy());
    }
}