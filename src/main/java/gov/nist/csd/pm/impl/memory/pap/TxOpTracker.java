package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.tx.Transactional;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TxOpTracker {

    private final List<Operation> operations;

    public TxOpTracker() {
        operations = new ArrayList<>();
    }

    public List<Operation> getOperations() {
        List<Operation> copy = new ArrayList<>(operations);

        Collections.reverse(copy);

        return copy;
    }

    public void trackOp(MemoryTx tx, Operation op) {
        if (!tx.isActive()) {
            return;
        }

        this.operations.add(op);
    }

    public void clearOps() {
        this.operations.clear();
    }
}
