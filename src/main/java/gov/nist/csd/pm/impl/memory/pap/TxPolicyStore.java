package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.PolicySerializer;

class TxPolicyStore implements Policy, BaseMemoryTx {

    private final MemoryPolicyStore memoryPolicyStore;

    protected TxPolicyEventTracker txPolicyEventTracker;

    private TxGraph txGraph;
    private TxProhibitions txProhibitions;
    private TxObligations txObligations;
    private TxUserDefinedPML txUserDefinedPML;

    public TxPolicyStore(MemoryPolicyStore txStore) {
        memoryPolicyStore = txStore;
        txPolicyEventTracker = new TxPolicyEventTracker();
        txGraph = new TxGraph(txPolicyEventTracker, (MemoryGraph) txStore.graph());
        txProhibitions = new TxProhibitions(txPolicyEventTracker, (MemoryProhibitions) txStore.prohibitions());
        txObligations = new TxObligations(txPolicyEventTracker, (MemoryObligations) txStore.obligations());
        txUserDefinedPML = new TxUserDefinedPML(txPolicyEventTracker, (MemoryUserDefinedPML) txStore.userDefinedPML());
    }

    public void clearEvents() {
        txPolicyEventTracker = new TxPolicyEventTracker();
    }

    @Override
    public TxGraph graph() {
        return txGraph;
    }

    @Override
    public TxProhibitions prohibitions() {
        return txProhibitions;
    }

    @Override
    public TxObligations obligations() {
        return txObligations;
    }

    @Override
    public TxUserDefinedPML userDefinedPML() {
        return txUserDefinedPML;
    }

    @Override
    public String serialize(PolicySerializer policySerializer) throws PMException {
        return memoryPolicyStore.serialize(policySerializer);
    }

    @Override
    public void deserialize(UserContext author, String input, PolicyDeserializer policyDeserializer)
            throws PMException {
        memoryPolicyStore.deserialize(author, input, policyDeserializer);
    }

    @Override
    public void reset() throws PMException {
        memoryPolicyStore.reset();
        clearEvents();
    }

    @Override
    public void rollback() throws PMException {
        clearEvents();
    }

}
