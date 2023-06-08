package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.GraphModifierTest;
import gov.nist.csd.pm.pap.query.GraphQuerierTest;

public class MemoryPolicyQuerierTest {

}

class MemoryGraphQuerierTest extends GraphQuerierTest {

    @Override
    public TestContext initTest() throws PMException {
        MemoryPAP pap = new MemoryPAP();
        return new TestContext(pap);
    }
}
