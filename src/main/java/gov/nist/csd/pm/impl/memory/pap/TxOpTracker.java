package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.op.Operation;

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

    public void trackOp(Operation op) {
        this.operations.add(op);
    }
}
