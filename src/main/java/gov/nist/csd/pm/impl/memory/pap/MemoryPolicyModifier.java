package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.modification.*;
import gov.nist.csd.pm.common.exception.PMException;

import static gov.nist.csd.pm.pap.AdminPolicy.Verifier;
import static gov.nist.csd.pm.pap.AdminPolicy.verify;
import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.common.graph.node.NodeType.PC;

public class MemoryPolicyModifier extends PolicyModifier implements Verifier {

    private MemoryPolicy policy;
    private MemoryPolicyQuerier querier;

    public MemoryPolicyModifier(MemoryPolicy policy) {
        this.policy = policy;
        this.querier = new MemoryPolicyQuerier(policy);
    }

    @Override
    public GraphModifier graph() {
        return policy.graph();
    }

    @Override
    public ProhibitionsModifier prohibitions() {
        return policy.prohibitions();
    }

    @Override
    public ObligationsModifier obligations() {
        return policy.obligations();
    }

    @Override
    public PMLModifier pml() {
        return policy.pml();
    }

    @Override
    public MemoryPolicyQuerier query() {
        return querier;
    }

    @Override
    public void beginTx() throws PMException {
        policy.beginTx();
    }

    @Override
    public void commit() throws PMException {
        policy.commit();
    }

    @Override
    public void rollback() throws PMException {
        policy.rollback();
    }
}
