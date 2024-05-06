package gov.nist.csd.pm.impl.memory.pdp;

import gov.nist.csd.pm.pap.PolicyModifier;
import gov.nist.csd.pm.pap.query.PolicyQuery;

public class MemoryPolicyReviewer implements PolicyQuery {

    private final MemoryAccessReviewer accessReviewer;
    private final MemoryGraphReviewer graphReviewer;
    private final MemoryProhibitionsReviewer prohibitionsReviewer;
    private final MemoryObligationsReviewer memoryObligationsReviewer;

    public MemoryPolicyReviewer(PolicyModifier policyStore) {
        this.accessReviewer = new MemoryAccessReviewer(policyStore);
        this.graphReviewer = new MemoryGraphReviewer(policyStore);
        this.prohibitionsReviewer = new MemoryProhibitionsReviewer(policyStore);
        this.memoryObligationsReviewer = new MemoryObligationsReviewer(policyStore, graphReviewer);
    }

    @Override
    public MemoryAccessReviewer access() {
        return accessReviewer;
    }

    @Override
    public MemoryGraphReviewer graph() {
        return graphReviewer;
    }

    @Override
    public MemoryProhibitionsReviewer prohibitions() {
        return prohibitionsReviewer;
    }

    @Override
    public MemoryObligationsReviewer obligations() {
        return memoryObligationsReviewer;
    }
}
