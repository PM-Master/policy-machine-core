package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.pap.exception.PMException;
import gov.nist.csd.pm.pap.obligation.EventPattern;
import gov.nist.csd.pm.pap.obligation.Rule;
import gov.nist.csd.pm.pap.pml.pattern.Pattern;
import gov.nist.csd.pm.pap.exception.*;
import gov.nist.csd.pm.pap.pml.pattern.operand.OperandPatternExpression;
import gov.nist.csd.pm.pap.store.PolicyStore;

import java.util.*;

public class ObligationsModifier extends Modifier implements ObligationsModification {

    public ObligationsModifier(PolicyStore store) {
        super(store);
    }

    public ObligationsModifier(Modifier modifier) {
        super(modifier);
    }

    @Override
    public void createObligation(String author, String name, List<Rule> rules) throws PMException {
        checkCreateInput(author, name, rules);

        store.obligations().createObligation(author, name, new ArrayList<>(rules));
    }

    @Override
    public void deleteObligation(String name) throws PMException {
        if(!checkDeleteInput(name)) {
            return;
        }

        store.obligations().deleteObligation(name);
    }

    /**
     * Check the obligation being created.
     *
     * @param author The author of the obligation.
     * @param name   The name of the obligation.
     * @param rules  The rules of the obligation.
     * @throws PMException If any PM related exceptions occur in the implementing class.
     */
    protected void checkCreateInput(String author, String name, Collection<Rule> rules) throws PMException {
        if (store.obligations().obligationExists(name)) {
            throw new ObligationNameExistsException(name);
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
        if (!store.obligations().obligationExists(name)) {
            return false;
        }

        return true;
    }

    private void checkAuthorExists(String author) throws PMException {
        if (!store.graph().nodeExists(author)) {
            throw new NodeDoesNotExistException(author);
        }
    }

    private void checkEventPatternAttributesExist(Collection<Rule> rules) throws PMException {
        for (Rule rule : rules) {
            EventPattern event = rule.getEventPattern();

            // check subject pattern
            Pattern pattern = event.getSubjectPattern();
            pattern.checkReferencedNodesExist(store.graph());

            // check operand patterns
            for (Map.Entry<String, List<OperandPatternExpression>> operandPattern : event.getOperandPatterns().entrySet()) {
                for (OperandPatternExpression operandPatternExpression : operandPattern.getValue()) {
                    operandPatternExpression.checkReferencedNodesExist(store.graph());
                }
            }
        }
    }
}