package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.modification.ObligationsModification;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.exception.PMRuntimeException;
import gov.nist.csd.pm.pap.op.obligation.CreateObligationOp;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.List;

public class TxObligationsModification implements ObligationsModification, BaseMemoryTx {

    private final TxOpTracker txOpTracker;
    private final MemoryObligationsModification memoryObligationsStore;

    public TxObligationsModification(TxOpTracker txOpTracker, MemoryObligationsModification memoryObligationsStore) {
        this.txOpTracker = txOpTracker;
        this.memoryObligationsStore = memoryObligationsStore;
    }
    @Override
    public void create(UserContext author, String name, Rule... rules) {
        txOpTracker.trackOp(new CreateObligationOp(author, name, List.of(rules)));
    }

    @Override
    public void rollback() {
        List<Operation> events = txOpTracker.getOperations();
        for (Operation event : events) {
            try {
                TxCmd<MemoryObligationsModification> txCmd = (TxCmd<MemoryObligationsModification>) TxCmd.eventToCmd(event);
                txCmd.rollback(memoryObligationsStore);
            } catch (PMException e) {
                // throw runtime exception because there is noway back if the rollback fails
                throw new PMRuntimeException("", e);
            }
        }
    }

    @Override
    public void update(UserContext author, String name, Rule... rules) throws PMException {
        txOpTracker.trackOp(new TxOps.MemoryUpdateObligationOp(
                new Obligation(author, name, List.of(rules)),
                memoryObligationsStore.get(name)
        ));
    }

    @Override
    public void delete(String name) throws PMException {
            txOpTracker.trackOp(new TxOps.MemoryDeleteObligationOp(memoryObligationsStore.get(name)));
    }

    @Override
    public List<Obligation> getAll() {
        return null;
    }

    @Override
    public boolean exists(String name) {
        return false;
    }

    @Override
    public Obligation get(String name) {
        return null;
    }

}
