package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.OperationExistsException;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.*;

public abstract class OperationsQuerier implements OperationsQuery{

    protected Map<String, Operation<?>> transientAdminOps;

    public OperationsQuerier() {
        this.transientAdminOps = new HashMap<>();
    }

    protected abstract Operation<?> getInternal(String op) throws PMException;
    protected abstract boolean operationExists(String op) throws PMException;
    protected abstract Collection<Operation<?>> getAllInternal() throws PMException;

    public Map<String, Operation<?>> getTransientAdminOps() {
        return transientAdminOps;
    }

    public void setTransientAdminOps(Collection<Operation<?>> transientAdminOps) {
        this.transientAdminOps = new HashMap<>();

        for (Operation<?> op : transientAdminOps) {
            this.transientAdminOps.put(op.getOpName(), op);
        }
    }

    @Override
    public Operation<?> getAdminOperation(String name) throws PMException {
        checkOperationExists(name);

        return getInternal(name);
    }

    @Override
    public Collection<Operation<?>> getAdminOperations() throws PMException {
        ArrayList<Operation<?>> operations = new ArrayList<>(transientAdminOps.values());
        operations.addAll(getAllInternal());
        return operations;
    }

    protected void checkOperationExists(String op) throws PMException {
        if (!operationExists(op)) {
            throw new OperationExistsException(op);
        }
    }
}
