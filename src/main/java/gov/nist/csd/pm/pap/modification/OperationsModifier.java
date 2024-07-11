package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.OperationDoesNotExistException;
import gov.nist.csd.pm.pap.op.Operation;

public abstract class OperationsModifier extends Modifier implements OperationsModification{

    protected abstract void createAdminOperationInternal(Operation operation);
    protected abstract void deleteAdminOperationInternal(String operation);

    @Override
    public void createAdminOperation(Operation operation) throws PMException {
        checkCreateInput(operation);

        createAdminOperationInternal(operation);
    }

    @Override
    public void deleteAdminOperation(String operation) throws PMException {
        checkDeleteInput(operation);

        deleteAdminOperationInternal(operation);
    }

    protected void checkCreateInput(Operation operation) throws PMException {
        try {
            query().operations().getAdminOperation(operation.getOpName());

            throw new OperationDoesNotExistException(operation.getOpName());
        } catch (OperationDoesNotExistException e) {
            // do nothing if the operation does not exist as this is the desired state
        }
    }

    protected boolean checkDeleteInput(String operation) throws PMException {
        try {
            query().operations().getAdminOperation(operation);
            return true;
        } catch (OperationDoesNotExistException e) {
            return false;
        }
    }
}
