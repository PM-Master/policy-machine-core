package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.tx.Transactional;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.PreparedOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TxOpTracker {

    private final List<PreparedOperation<?>> operations;

    public TxOpTracker() {
        operations = new ArrayList<>();
    }

    public List<PreparedOperation<?>> getOperations() {
        List<PreparedOperation<?>> copy = new ArrayList<>(operations);

        Collections.reverse(copy);

        return copy;
    }

    public void trackOp(MemoryTx tx, PreparedOperation<?> op) {
        if (!tx.isActive()) {
            return;
        }

        this.operations.add(op);
    }

    public void clearOps() {
        this.operations.clear();
    }
}
