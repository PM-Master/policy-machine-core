package gov.nist.csd.pm.common.tx;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.exception.NodeNameExistsException;
import gov.nist.csd.pm.common.exception.PMException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static gov.nist.csd.pm.common.tx.TxRunner.runTx;
import static org.junit.jupiter.api.Assertions.*;

class TxHandlerRunnerTest {

    @Test
    void testRunTx() throws PMException {
        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);

        runTx(pap, () -> {
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
        });

        assertTrue(pap.policy().graph().nodeExists("pc1"));

        assertThrows(NodeNameExistsException.class, () -> runTx(pap, () -> {
            pap.policy().graph().deleteNode("pc1");
            pap.policy().graph().createPolicyClass("pc2", new HashMap<>());
            // expect error and rollback
            pap.policy().graph().createPolicyClass("pc2", new HashMap<>());
        }));

        assertTrue(pap.policy().graph().nodeExists("pc1"));
        assertFalse(pap.policy().graph().nodeExists("pc2"));
    }

}