package gov.nist.csd.pm.pap.tx;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.pap.exception.NodeNameExistsException;
import gov.nist.csd.pm.common.exception.PMException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static gov.nist.csd.pm.common.tx.TxRunner.runTx;
import static org.junit.jupiter.api.Assertions.*;

class TxHandlerRunnerTest {

    @Test
    void testRunTx() throws PMException {
        PAP pap = new PAP(new MemoryPolicyStore());
        runTx(pap, () -> {
            pap.graph().createPolicyClass("pc1", new HashMap<>());
        });

        assertTrue(pap.graph().nodeExists("pc1"));

        assertThrows(NodeNameExistsException.class, () -> runTx(pap, () -> {
            pap.graph().deleteNode("pc1");
            pap.graph().createPolicyClass("pc2", new HashMap<>());
            // expect error and rollback
            pap.graph().createPolicyClass("pc2", new HashMap<>());
        }));

        assertTrue(pap.graph().nodeExists("pc1"));
        assertFalse(pap.graph().nodeExists("pc2"));
    }

}