package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class ObligationsModifier extends Modifier implements ObligationsModification {

    protected abstract void createInternal(UserContext author, String name, Collection<Rule> rules) throws PMException;
    protected abstract void updateInternal(UserContext author, String name, Collection<Rule> rules) throws PMException;
    protected abstract void deleteInternal(String name) throws PMException;

    @Override
    public void create(UserContext author, String name, Collection<Rule> rules) throws PMException {
        checkCreateInput(author, name, rules);

        createInternal(author, name, rules);
    }

    @Override
    public void update(UserContext author, String name, Collection<Rule> rules) throws PMException {
        checkUpdateInput(author, name, rules);

        updateInternal(author, name, rules);
    }

    @Override
    public void delete(String name) throws PMException {
        if(!checkDeleteInput(name)) {
            return;
        }

        deleteInternal(name);
    }

    /**
     * Check the obligation being created.
     *
     * @param author The author of the obligation.
     * @param name   The name of the obligation.
     * @param rules  The rules of the obligation.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkCreateInput(UserContext author, String name, Collection<Rule> rules) throws PMException {
        if (query().obligations().exists(name)) {
            throw new ObligationNameExistsException(name);
        }

        checkAuthorExists(author);
        checkEventPatternAttributesExist(rules);
    }

    /**
     * Check the obligation being created.
     *
     * @param author The author of the obligation.
     * @param name   The name of the obligation.
     * @param rules  The rules of the obligation.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkUpdateInput(UserContext author, String name, Collection<Rule> rules) throws PMException {
        if (!query().obligations().exists(name)) {
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
        if (!query().obligations().exists(name)) {
            return false;
        }

        return true;
    }

    private void checkAuthorExists(UserContext author) throws PMException {
        if (!query().graph().nodeExists(author.getUser())) {
            throw new NodeDoesNotExistException(author.getUser());
        }
    }

    private void checkEventPatternAttributesExist(Collection<Rule> rules) throws PMException {
        for (Rule rule : rules) {
            EventPattern event = rule.getEventPattern();

            // check subject pattern
            Pattern pattern = event.getSubjectPattern();
            pattern.checkReferencedNodesExist(query());

            // check operand patterns
            for (Pattern operandPattern : event.getOperandPatterns()) {
                operandPattern.checkReferencedNodesExist(query());
            }
        }
    }
}
