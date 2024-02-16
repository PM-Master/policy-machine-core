package gov.nist.csd.pm.pdp.memory;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.memory.MemoryPolicyStore;
import gov.nist.csd.pm.pdp.AccessReviewerTest;
import gov.nist.csd.pm.policy.review.AccessReview;
import gov.nist.csd.pm.policy.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.AccessRightSet;
import gov.nist.csd.pm.policy.model.access.UserContext;
import gov.nist.csd.pm.policy.model.audit.Explain;
import gov.nist.csd.pm.policy.model.audit.Path;
import gov.nist.csd.pm.policy.model.audit.PolicyClass;
import gov.nist.csd.pm.policy.model.graph.relationships.Association;
import gov.nist.csd.pm.policy.model.prohibition.ContainerCondition;
import gov.nist.csd.pm.policy.model.prohibition.Prohibition;
import gov.nist.csd.pm.policy.model.prohibition.ProhibitionSubject;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

import static gov.nist.csd.pm.policy.model.access.AdminAccessRights.*;
import static gov.nist.csd.pm.policy.model.access.AdminAccessRights.allAdminAccessRights;
import static org.junit.jupiter.api.Assertions.*;

class MemoryAccessReviewerTest extends AccessReviewerTest {

    @Override
    public TestContext initTest() throws PMException {
        PAP pap = new PAP(new MemoryPolicyStore());
        return new TestContext(new MemoryAccessReviewer(pap), pap);
    }
}