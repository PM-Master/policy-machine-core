package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.common.exception.PMException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PMLBootstrapperTest {

    @Test
    void test() throws PMException {
        PAP pap = new PAP(new MemoryPolicyStore());
        PDP pdp = new PDP(pap, new MemoryPolicyReviewer(pap));

        String input = """
                const read = "read"
                const write = "write"
                set resource access rights [read, write]
                
                create pc "pc1"
                create ua "ua1" assign to ["pc1"]
                create oa "oa1" assign to ["pc1"]
                
                associate "ua1" and "oa1" with [read]
                
                create user "u1" assign to ["ua1"]
                """;

        pdp.bootstrap(new PMLBootstrapper(new UserContext("u1"), input));

        assertTrue(pap.graph().nodeExists("pc1"));
        assertTrue(pap.graph().nodeExists("ua1"));
        assertTrue(pap.graph().nodeExists("oa1"));
        assertTrue(pap.graph().nodeExists("u1"));
    }

}