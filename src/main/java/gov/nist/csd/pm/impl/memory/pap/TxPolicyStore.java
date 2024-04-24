package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.PolicySerializer;

class TxPolicyStore implements Policy, BaseMemoryTx {

    private final MemoryPolicyStore memoryPolicyStore;

    protected TxOpTracker txOpTracker;

    private TxGraph txGraph;
    private TxProhibitions txProhibitions;
    private TxObligations txObligations;
    private TxUserDefinedPML txUserDefinedPML;

    public TxPolicyStore(MemoryPolicyStore txStore) {
        memoryPolicyStore = txStore;
        txOpTracker = new TxOpTracker();
        txGraph = new TxGraph(txOpTracker, (MemoryGraph) txStore.graph());
        txProhibitions = new TxProhibitions(txOpTracker, (MemoryProhibitions) txStore.prohibitions());
        txObligations = new TxObligations(txOpTracker, (MemoryObligations) txStore.obligations());
        txUserDefinedPML = new TxUserDefinedPML(txOpTracker, (MemoryUserDefinedPML) txStore.userDefinedPML());
    }

    public void clearOps() {
        txOpTracker = new TxOpTracker();
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
        clearOps();
    }

    @Override
    public void rollback() throws PMException {
        clearOps();
    }

}
