package gov.nist.csd.pm.pap.memory;

import gov.nist.csd.pm.policy.GraphReader;
import gov.nist.csd.pm.policy.ObligationsReader;
import gov.nist.csd.pm.policy.ProhibitionsReader;
import gov.nist.csd.pm.policy.events.*;
import gov.nist.csd.pm.policy.exceptions.PMException;

public class MemoryPolicyStoreEventHandler extends PolicyEventHandler {

    public MemoryPolicyStoreEventHandler(MemoryPolicyStore store) {
        super(store);
    }

    public GraphReader graph() {
        return policy.graph();
    }

    public ProhibitionsReader prohibitions() {
        return policy.prohibitions();
    }

    public ObligationsReader obligations() {
        return policy.obligations();
    }

    @Override
    public synchronized void handlePolicyEvent(PolicyEvent event) throws PMException {
        if (event instanceof PolicySynchronizationEvent policySynchronizationEvent) {
            policy = new MemoryPolicyStore(policySynchronizationEvent);
        } else {
            super.handlePolicyEvent(event);
        }
    }
}
