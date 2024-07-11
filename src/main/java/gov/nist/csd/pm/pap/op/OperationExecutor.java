package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;

import java.util.List;

public interface OperationExecutor<T> {

    T execute(PAP pap, List<Object> operands) throws PMException;

}
