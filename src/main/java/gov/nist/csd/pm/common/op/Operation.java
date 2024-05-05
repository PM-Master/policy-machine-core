package gov.nist.csd.pm.common.op;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.op.pattern.Pattern;
import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.UserContext;

import java.io.Serializable;
import java.util.List;

public abstract class Operation implements Serializable {

    protected static Object[] operands(Object ... ops) {
        return ops;
    }

    private final Object[] operands;

    public Operation(Object[] operands) {
        this.operands = operands;
    }

    public abstract String getOpName();

    // TODO - need capmap and can make it not abstract
    //  public abstract boolean canRespond(UserContext userCtx, PAP pap) throws PMException;

    public Object[] getOperands() {
        return operands;
    }

    /*public boolean matches(EventPattern pattern, GraphReview graphReview) throws PMException {
        boolean opPatternMatches = operationMatches(pattern.operationPattern(), graphReview);
        boolean operandPatternsMatch = operandsMatch(graphReview, pattern.operandPatterns());

        return opPatternMatches && operandPatternsMatch;
    }

    private boolean operationMatches(Pattern<String> operationPattern, GraphReview graphReview) throws PMException {
        return operationPattern.matches(getOpName(), graphReview);
    }

    private boolean operandsMatch(GraphReview graphReview, List<Pattern<Object>> operandPatterns) throws PMException {
        // if there are more values provided than patterns, they cannot match
        // if there are more patterns than values than there still might be a match
        if (operands.length > operandPatterns.size()) {
            return false;
        }

        for (int i = 0; i < operands.length; i++) {
            Object operandValue = operands[i];
            Pattern<Object> operandPattern = operandPatterns.get(i);

            if (!operandPattern.matches(operandValue, graphReview)) {
                return false;
            }
        }

        return true;
    }*/
}
