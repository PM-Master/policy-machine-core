package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.Collection;

public interface OperationsModification {

    void setResourceOperations(AccessRightSet resourceOperations) throws PMException;
    void createAdminOperation(Operation operation) throws PMException;
    void deleteAdminOperation(String operation) throws PMException;

}
