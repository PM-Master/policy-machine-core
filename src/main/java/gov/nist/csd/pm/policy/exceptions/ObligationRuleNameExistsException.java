package gov.nist.csd.pm.policy.exceptions;

public class ObligationRuleNameExistsException extends PMException {
    public ObligationRuleNameExistsException(String obligationName, String ruleName) {
        super("A rule with the name " + ruleName + " already exists in the obligation " + obligationName);
    }
}
