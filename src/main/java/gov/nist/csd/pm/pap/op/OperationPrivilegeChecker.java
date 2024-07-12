package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Map;

public interface OperationPrivilegeChecker {

    void canExecute(
            PAP pap,
            UserContext userCtx,
            String opName,
            Map<String, RequiredCapability> capMap,
            Map<String, Object> operands) throws PMException;

}
