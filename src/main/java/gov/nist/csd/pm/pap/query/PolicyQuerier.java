package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.pap.op.Operation;

import java.util.Collection;

public abstract class PolicyQuerier implements PolicyQuery{

    public void setTransientAdminOperations(Collection<Operation> ops) {
        operations().setTransientAdminOps(ops);
    }

    @Override
    public abstract AccessQuerier access();

    @Override
    public abstract GraphQuerier graph();

    @Override
    public abstract ProhibitionsQuerier prohibitions();

    @Override
    public abstract ObligationsQuerier obligations();

    @Override
    public abstract OperationsQuerier operations();
}
