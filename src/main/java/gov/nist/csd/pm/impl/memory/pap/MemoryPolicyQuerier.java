package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.query.*;

public class MemoryPolicyQuerier extends PolicyQuerier {

    private MemoryAccessQuerier accessQuerier;
    private MemoryGraphQuerier graphQuerier;
    private MemoryProhibitionsQuerier prohibitionsQuerier;
    private MemoryObligationsQuerier obligationsQuerier;

    public MemoryPolicyQuerier(MemoryPolicy memoryPolicy) {
        this.graphQuerier = new MemoryGraphQuerier(memoryPolicy);
        this.prohibitionsQuerier = new MemoryProhibitionsQuerier(memoryPolicy, graphQuerier);
        this.obligationsQuerier = new MemoryObligationsQuerier(memoryPolicy);
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
}
