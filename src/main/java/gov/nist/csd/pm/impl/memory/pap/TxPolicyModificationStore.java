package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.PolicySerializer;

class TxPolicyModificationStore implements PolicyModification, BaseMemoryTx {

    private final MemoryPolicyModifier memoryPolicyStore;

    protected TxOpTracker txOpTracker;

    private TxGraphModification txGraph;
    private TxProhibitionsModification txProhibitions;
    private TxObligationsModification txObligations;
    private TxPMLModification txUserDefinedPML;

    public TxPolicyModificationStore(MemoryPolicyModifier txStore) {
        memoryPolicyStore = txStore;
        txOpTracker = new TxOpTracker();
        txGraph = new TxGraphModification(txOpTracker, (MemoryGraphModification) txStore.graph());
        txProhibitions = new TxProhibitionsModification(txOpTracker, (MemoryProhibitionsModification) txStore.prohibitions());
        txObligations = new TxObligationsModification(txOpTracker, (MemoryObligationsModification) txStore.obligations());
        txUserDefinedPML = new TxPMLModification(txOpTracker, (MemoryPMLModification) txStore.pml());
    }

    public void clearOps() {
        txOpTracker = new TxOpTracker();
    }

    @Override
    public TxGraphModification graph() {
        return txGraph;
    }

    @Override
    public TxProhibitionsModification prohibitions() {
        return txProhibitions;
    }

    @Override
    public TxObligationsModification obligations() {
        return txObligations;
    }

    @Override
    public TxPMLModification pml() {
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
