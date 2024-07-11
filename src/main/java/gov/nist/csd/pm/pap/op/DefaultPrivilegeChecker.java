package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.exception.InvalidOperationException;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Collection;
import java.util.List;

public class DefaultPrivilegeChecker implements OperationPrivilegeChecker {

    @Override
    public void canExecute(PAP pap, UserContext userCtx, String opName, List<RequiredCapability> capMap, List<Object> operands) throws PMException {
        if (operands.size() != capMap.size()) {
            throw new InvalidOperationException(opName, capMap, operands);
        }

        for (int i = 0; i < capMap.size(); i++) {
            RequiredCapability reqCap = capMap.get(i);

            // skip operands that do not have a required capability
            if (reqCap.caps().isEmpty()) {
                continue;
            }

            Object operand = operands.get(i);

            // policy element operands can be strings or collections of strings
            if (operand instanceof String strOp) {
                PrivilegeChecker.check(pap, userCtx, strOp, reqCap.capsArray());
            } else {
                if (operand instanceof Collection<?> colOp) {
                    for (Object o : colOp) {
                        if (o instanceof String strColOp) {
                            PrivilegeChecker.check(pap, userCtx, strColOp, reqCap.capsArray());
                        }
                    }
                }
            }
        }
    }
}
