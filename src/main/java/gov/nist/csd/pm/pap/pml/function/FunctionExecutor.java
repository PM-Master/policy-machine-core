package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;

/**
 * This class is not serializable because code defined in the exec method could use dependencies not available
 * on the target system.
 */
public interface FunctionExecutor {

    Value exec(ExecutionContext ctx, PAP pap) throws PMException;

}
