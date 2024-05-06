package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.query.PolicyQuery;
import gov.nist.csd.pm.pdp.UserContext;

import java.util.HashSet;
import java.util.Set;

public abstract class ObligationsModifier extends Modifier {


    public ObligationsModifier(PolicyQuery policyQuery) {
        super(policyQuery);
    }

    /**
     * Check the obligation being created.
     *
     * @param author The author of the obligation.
     * @param name The name of the obligation.
     * @param rules The rules of the obligation.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkCreateInput(UserContext author, String name, Rule... rules) throws PMException {
        if (querier.obligations().exists(name)) {
            throw new ObligationNameExistsException(name);
        }

        checkAuthorExists(author);
        checkEventPatternAttributesExist(rules);
    }

    /**
     * Check the obligation being created.
     *
     * @param author     The author of the obligation.
     * @param name         The name of the obligation.
     * @param rules      The rules of the obligation.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkUpdateInput(UserContext author, String name, Rule... rules) throws PMException {
        if (!querier.obligations().exists(name)) {
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

        checkAuthorExists(author);
        checkEventPatternAttributesExist(rules);
    }

    /**
     * Check if the obligation exists. If it doesn't, return false to indicate to the caller that execution should not
     * proceed.
     *
     * @param name The name of the obligation.
     * @return True if the execution should proceed, false otherwise.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected boolean checkDeleteInput(String name) throws PMException {
        if (!querier.obligations().exists(name)) {
            return false;
        }

        return true;
    }

    /**
     * Check if the obligation exists.
     * @param name The obligation name.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkGetInput(String name) throws PMException {
        if (!querier.obligations().exists(name)) {
            throw new ObligationDoesNotExistException(name);
        }
    }

    private void checkAuthorExists(UserContext author) throws PMException {
        if (!querier.graph().nodeExists(author.getUser())) {
            throw new NodeDoesNotExistException(author.getUser());
        }
    }

    private void checkEventPatternAttributesExist(Rule... rules) throws PMException {
        for (Rule rule : rules) {
            EventPattern event = rule.getEventPattern();

            // check subject pattern
            Pattern<String> pattern = event.getSubjectPattern();
            if(!pattern.checkReferencedPolicyEntitiesExist(querier)) {
                throw new PolicyEntityDoesNotExistException("policy entity in event subject pattern does not exist");
            }

            // TODO add check for operations - not yet implemented
            // pattern = event.operationPattern();

            // check operand patterns
            for (Pattern<Object> operandPattern : event.getOperandPatterns()) {
                if(!operandPattern.checkReferencedPolicyEntitiesExist(querier)) {
                    throw new PolicyEntityDoesNotExistException("policy entity in operand pattern does not exist");
                }
            }
        }
    }
}
