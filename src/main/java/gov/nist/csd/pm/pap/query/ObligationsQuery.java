package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Collection;
import java.util.List;

public interface ObligationsQuery {

    /**
     * Get all obligations.
     *
     * @return All obligations.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<Obligation> getAll() throws PMException;

    /**
     * Check if an obligation exists with the given name.
     *
     * @param name The obligation to check.
     * @return True if the obligation exists with the given name, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    boolean exists(String name) throws PMException;

    /**
     * Get the obligation associated with the given name.
     *
     * @param name The name of the obligation to get.
     * @return The obligation object associated with the given name.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Obligation get(String name) throws PMException;

    /**
     * Get the obligations created by the given author.
     *
     * @param userCtx The user context representing the author to search for.
     * @return A list of Obligation objects.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    Collection<Obligation> getObligationsWithAuthor(UserContext userCtx) throws PMException;
    
}
