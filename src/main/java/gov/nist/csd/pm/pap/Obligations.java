package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.op.pattern.Pattern;
import gov.nist.csd.pm.common.op.pattern.ReferencedPolicyEntities;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.pap.exception.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * NGAC obligation methods.
 */
public interface Obligations {

    /**
     * Create a new obligation with the given author, name, and rules. The author of the obligation is the user that the
     * responses will be executed as in the EPP. This means the author will need the privileges to carry out each action
     * in the response at the time it's executed. If they do not have sufficient privileges no action in the response
     * will be executed. <p>
     *
     * @param author The user/process that is creating the obligation.
     * @param name The name of the obligation.
     * @param rules The rules of the obligation.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void create(UserContext author, String name, Rule... rules) throws PMException;

    /**
     * Update the author and rules of the obligation with the given name. This will overwrite any existing rules to the rules
     * provided and update the existing author. <p>
     *
     * @param author The user/process that updated the obligation.
     * @param name The name of the obligation to update.
     * @param rules The updated obligation rules.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void update(UserContext author, String name, Rule... rules) throws PMException;

    /**
     * Delete the obligation with the given name. If the obligation does not exist, no exception is thrown as this is
     * the desired state. <p>
     *
     * @param name The name of the obligation to delete.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    void delete(String name) throws PMException;

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

    /**
     * Check the obligation being created.
     *
     * @param graph The GraphStore used to check if the author and event pattern policy elements exist.
     * @param author The author of the obligation.
     * @param name The name of the obligation.
     * @param rules The rules of the obligation.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkCreateInput(Graph graph, UserContext author, String name, Rule... rules) throws PMException {
        if (exists(name)) {
            throw new ObligationNameExistsException(name);
        }

        checkAuthorExists(graph, author);
        checkEventPatternAttributesExist(graph, rules);
    }

    /**
     * Check the obligation being created.
     *
     * @param graph The GraphStore used to check if the author and event pattern policy elements exist.
     * @param author     The author of the obligation.
     * @param name         The name of the obligation.
     * @param rules      The rules of the obligation.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkUpdateInput(Graph graph, UserContext author, String name, Rule... rules) throws PMException {
        if (!exists(name)) {
            throw new ObligationDoesNotExistException(name);
        }

        // check that there are no duplicate rule names
        Set<String> ruleNames = new HashSet<>();
        for (Rule rule : rules) {
            if (ruleNames.contains(rule.getName())) {
                throw new ObligationRuleNameExistsException(name, rule.getName());
            }

            ruleNames.add(rule.getName());
        }

        checkAuthorExists(graph, author);
        checkEventPatternAttributesExist(graph, rules);
    }

    /**
     * Check if the obligation exists. If it doesn't, return false to indicate to the caller that execution should not
     * proceed.
     *
     * @param name The name of the obligation.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default boolean checkDeleteInput(String name) throws PMException {
        if (!exists(name)) {
            return false;
        }

        return true;
    }

    /**
     * Check if the obligation exists.
     * @param name The obligation name.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    default void checkGetInput(String name) throws PMException {
        if (!exists(name)) {
            throw new ObligationDoesNotExistException(name);
        }
    }

    private void checkAuthorExists(Graph graph, UserContext author) throws PMException {
        if (!graph.nodeExists(author.getUser())) {
            throw new NodeDoesNotExistException(author.getUser());
        }
    }

    private void checkEventPatternAttributesExist(Graph graph, Rule... rules) throws PMException {
        for (Rule rule : rules) {
            EventPattern event = rule.getEventPattern();

            // check subject pattern
            Pattern<String> pattern = event.getSubjectPattern();
            if(!pattern.checkReferencedPolicyEntitiesExist(graph)) {
                throw new PolicyEntityDoesNotExistException("policy entity in event subject pattern does not exist");
            }

            // TODO add check for operations - not yet implemented
            // pattern = event.operationPattern();

            // check operand patterns
            for (Pattern<Object> operandPattern : event.getOperandPatterns()) {
                if(!operandPattern.checkReferencedPolicyEntitiesExist(graph)) {
                    throw new PolicyEntityDoesNotExistException("policy entity in operand pattern does not exist");
                }
            }
        }
    }
}
