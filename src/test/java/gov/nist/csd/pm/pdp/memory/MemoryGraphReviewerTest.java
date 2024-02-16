package gov.nist.csd.pm.pdp.memory;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.memory.MemoryPolicyStore;
import gov.nist.csd.pm.pdp.AccessReviewerTest;
import gov.nist.csd.pm.pdp.GraphReviewerTest;
import gov.nist.csd.pm.policy.review.GraphReview;
import gov.nist.csd.pm.policy.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.UserContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemoryGraphReviewerTest extends GraphReviewerTest {

    @Override
    public TestContext initTest() throws PMException {
        PAP pap = new PAP(new MemoryPolicyStore());
        return new TestContext(new MemoryGraphReviewer(pap), pap);
    }
}