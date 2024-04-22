package gov.nist.csd.pm.impl.memory.pdp;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyReview;

public class MemoryPolicyReviewer implements PolicyReview {

    private final MemoryAccessReviewer accessReviewer;
    private final MemoryGraphReviewer graphReviewer;
    private final MemoryProhibitionsReviewer prohibitionsReviewer;
    private final MemoryObligationsReviewer memoryObligationsReviewer;

    public MemoryPolicyReviewer(PAP pap) {
        this.accessReviewer = new MemoryAccessReviewer(pap);
        this.graphReviewer = new MemoryGraphReviewer(pap);
        this.prohibitionsReviewer = new MemoryProhibitionsReviewer(pap);
        this.memoryObligationsReviewer = new MemoryObligationsReviewer(pap, graphReviewer);
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
