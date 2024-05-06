package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;

import java.util.List;
import java.util.Map;

public interface ObligationsQuery {

    /**
     * Get all obligations.
     *
     * @return All obligations.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    List<Obligation> getAll() throws PMException;

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

    List<Obligation> getObligationsWithAuthor(UserContext userCtx) throws PMException;
    Map<String, List<Rule>> getRulesWithEventSubject(String subject) throws PMException;
    Map<String, List<Rule>> getRulesWithEventOperation(String operation) throws PMException;
    Map<String, List<Rule>> getRulesWithEventOperand(String target) throws PMException;
    List<Response> getMatchingEventResponses(EventContext eventCtx) throws PMException;

}
