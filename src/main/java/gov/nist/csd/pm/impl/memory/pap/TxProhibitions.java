package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.Prohibitions;
import gov.nist.csd.pm.pap.op.PolicyEvent;
import gov.nist.csd.pm.pap.op.prohibitions.CreateProhibitionEvent;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.exception.PMRuntimeException;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;

import java.util.List;
import java.util.Map;

public class TxProhibitions implements Prohibitions, BaseMemoryTx {

    private final TxPolicyEventTracker txPolicyEventTracker;
    private final MemoryProhibitions memoryProhibitionsStore;

    public TxProhibitions(TxPolicyEventTracker txPolicyEventTracker, MemoryProhibitions memoryProhibitionsStore) {
        this.txPolicyEventTracker = txPolicyEventTracker;
        this.memoryProhibitionsStore = memoryProhibitionsStore;
    }

    @Override
    public void rollback() {
        List<PolicyEvent> events = txPolicyEventTracker.getEvents();
        for (PolicyEvent event : events) {
            try {
                TxCmd<MemoryProhibitions> txCmd = (TxCmd<MemoryProhibitions>) TxCmd.eventToCmd(event);
                txCmd.rollback(memoryProhibitionsStore);
            } catch (PMException e) {
                // throw runtime exception because there is noway back if the rollback fails
                throw new PMRuntimeException("", e);
            }
        }
    }

    @Override
    public void create(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions) {
        txPolicyEventTracker.trackPolicyEvent(new CreateProhibitionEvent(name, subject, accessRightSet, intersection, List.of(containerConditions)));
    }

    @Override
    public void update(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions)
            throws PMException {
        txPolicyEventTracker.trackPolicyEvent(new TxEvents.MemoryUpdateProhibitionEvent(
                new Prohibition(name, subject, accessRightSet, intersection, List.of(containerConditions)),
                memoryProhibitionsStore.get(name)
        ));
    }

    @Override
    public void delete(String name) throws PMException {
        txPolicyEventTracker.trackPolicyEvent(new TxEvents.MemoryDeleteProhibitionEvent(memoryProhibitionsStore.get(name)));
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
