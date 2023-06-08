package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.query.*;

public class MemoryPolicyQuerier extends PolicyQuerier {

    private MemoryAccessQuerier accessQuerier;
    private MemoryGraphQuerier graphQuerier;
    private MemoryProhibitionsQuerier prohibitionsQuerier;
    private MemoryObligationsQuerier obligationsQuerier;
    private MemoryPMLQuerier pmlQuerier;

    public MemoryPolicyQuerier(MemoryPolicy memoryPolicy) {
        this.graphQuerier = new MemoryGraphQuerier(memoryPolicy);
        this.prohibitionsQuerier = new MemoryProhibitionsQuerier(memoryPolicy, graphQuerier);
        this.obligationsQuerier = new MemoryObligationsQuerier(memoryPolicy);
        this.pmlQuerier = new MemoryPMLQuerier(memoryPolicy);
        this.accessQuerier = new MemoryAccessQuerier(graphQuerier, prohibitionsQuerier);
    }

    @Override
    public MemoryAccessQuerier access() {
        return accessQuerier;
    }

    @Override
    public MemoryGraphQuerier graph() {
        return graphQuerier;
    }

    @Override
    public MemoryProhibitionsQuerier prohibitions() {
        return prohibitionsQuerier;
    }

    @Override
    public MemoryObligationsQuerier obligations() {
        return obligationsQuerier;
    }

    @Override
    public MemoryPMLQuerier pml() {
        return pmlQuerier;
    }
}
