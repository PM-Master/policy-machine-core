package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.PAP;

public class MemoryPAP extends PAP {

    private MemoryPolicy memoryPolicy;
    private MemoryPolicyModifier modifier;

    public MemoryPAP() throws PMException {
        this.memoryPolicy = new MemoryPolicy();
        this.modifier = new MemoryPolicyModifier(memoryPolicy);
        AdminPolicy.verify(modifier);
    }

    public MemoryPAP(MemoryPolicy memoryPolicy) throws PMException {
        this.memoryPolicy = memoryPolicy;
        this.modifier = new MemoryPolicyModifier(memoryPolicy);
        AdminPolicy.verify(modifier);
    }

    @Override
    public MemoryPolicyModifier modify() {
        return modifier;
    }

    @Override
    public MemoryPolicyQuerier query() {
        return modifier.query();
    }

    @Override
    public void reset() throws PMException {
        // reset
        memoryPolicy.reset();

        // admin policy
        AdminPolicy.verify(modifier);
    }

    @Override
    public void beginTx() throws PMException {
        memoryPolicy.beginTx();
    }

    @Override
    public void commit() throws PMException {
        memoryPolicy.commit();
    }

    @Override
    public void rollback() throws PMException {
        memoryPolicy.rollback();
    }
}
