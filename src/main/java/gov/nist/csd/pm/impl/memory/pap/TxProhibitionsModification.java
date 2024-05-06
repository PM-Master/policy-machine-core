package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.modification.ProhibitionsModification;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.prohibition.CreateProhibitionOp;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.exception.PMRuntimeException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;

import java.util.List;
import java.util.Map;

public class TxProhibitionsModification implements ProhibitionsModification, BaseMemoryTx {

    private final TxOpTracker txOpTracker;
    private final MemoryProhibitionsModification memoryProhibitionsStore;

    public TxProhibitionsModification(TxOpTracker txOpTracker, MemoryProhibitionsModification memoryProhibitionsStore) {
        this.txOpTracker = txOpTracker;
        this.memoryProhibitionsStore = memoryProhibitionsStore;
    }

    @Override
    public void rollback() {
        List<Operation> events = txOpTracker.getOperations();
        for (Operation event : events) {
            try {
                TxCmd<MemoryProhibitionsModification> txCmd = (TxCmd<MemoryProhibitionsModification>) TxCmd.eventToCmd(event);
                txCmd.rollback(memoryProhibitionsStore);
            } catch (PMException e) {
                // throw runtime exception because there is noway back if the rollback fails
                throw new PMRuntimeException("", e);
            }
        }
    }

    @Override
    public void create(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions) {
        txOpTracker.trackOp(new CreateProhibitionOp(name, subject, accessRightSet, intersection, List.of(containerConditions)));
    }

    @Override
    public void update(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions)
            throws PMException {
        txOpTracker.trackOp(new TxOps.MemoryUpdateProhibitionOp(
                new Prohibition(name, subject, accessRightSet, intersection, List.of(containerConditions)),
                memoryProhibitionsStore.get(name)
        ));
    }

    @Override
    public void delete(String name) throws PMException {
        txOpTracker.trackOp(new TxOps.MemoryDeleteProhibitionOp(memoryProhibitionsStore.get(name)));
    }

    @Override
    public Map<String, List<Prohibition>> getAll() {
        return null;
    }

    @Override
    public boolean exists(String name) {
        return false;
    }

    @Override
    public List<Prohibition> getWithSubject(String subject) {
        return null;
    }

    @Override
    public Prohibition get(String name) {
        return null;
    }
}
