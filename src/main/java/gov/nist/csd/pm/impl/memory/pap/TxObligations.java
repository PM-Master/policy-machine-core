package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.Obligations;
import gov.nist.csd.pm.pap.op.PolicyEvent;
import gov.nist.csd.pm.pap.op.obligations.CreateObligationEvent;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.exception.PMRuntimeException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.List;

public class TxObligations implements Obligations, BaseMemoryTx {

    private final TxPolicyEventTracker txPolicyEventTracker;
    private final MemoryObligations memoryObligationsStore;

    public TxObligations(TxPolicyEventTracker txPolicyEventTracker, MemoryObligations memoryObligationsStore) {
        this.txPolicyEventTracker = txPolicyEventTracker;
        this.memoryObligationsStore = memoryObligationsStore;
    }
    @Override
    public void create(UserContext author, String name, Rule... rules) {
        txPolicyEventTracker.trackPolicyEvent(new CreateObligationEvent(author, name, List.of(rules)));
    }

    @Override
    public void rollback() {
        List<PolicyEvent> events = txPolicyEventTracker.getEvents();
        for (PolicyEvent event : events) {
            try {
                TxCmd<MemoryObligations> txCmd = (TxCmd<MemoryObligations>) TxCmd.eventToCmd(event);
                txCmd.rollback(memoryObligationsStore);
            } catch (PMException e) {
                // throw runtime exception because there is noway back if the rollback fails
                throw new PMRuntimeException("", e);
            }
        }
    }

    @Override
    public void update(UserContext author, String name, Rule... rules) throws PMException {
        txPolicyEventTracker.trackPolicyEvent(new TxEvents.MemoryUpdateObligationEvent(
                new Obligation(author, name, List.of(rules)),
                memoryObligationsStore.get(name)
        ));
    }

    @Override
    public void delete(String name) throws PMException {
            txPolicyEventTracker.trackPolicyEvent(new TxEvents.MemoryDeleteObligationEvent(memoryObligationsStore.get(name)));
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
