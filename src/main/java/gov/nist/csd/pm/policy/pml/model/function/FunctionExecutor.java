package gov.nist.csd.pm.policy.pml.model.function;

import gov.nist.csd.pm.policy.Policy;
import gov.nist.csd.pm.policy.pml.model.context.ExecutionContext;
import gov.nist.csd.pm.policy.pml.model.expression.Value;
import gov.nist.csd.pm.policy.pml.model.scope.PMLScopeException;
import gov.nist.csd.pm.policy.exceptions.PMException;

public interface FunctionExecutor {

    Value exec(ExecutionContext ctx, Policy policy) throws PMException, PMLScopeException;

}
