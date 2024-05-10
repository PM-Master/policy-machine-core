package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.pattern.Pattern;

import java.io.Serializable;
import java.util.List;

public abstract class Operation implements Serializable {

    protected static Object[] operands(Object ... ops) {
        return ops;
    }

    public abstract String getOpName();

    // TODO - need capmap and can make it not abstract
    //  public abstract boolean canRespond(UserContext userCtx, PAP pap) throws PMException;

    public abstract Object[] getOperands();

    public boolean matches(EventPattern pattern, PAP pap) throws PMException {
        boolean opPatternMatches = operationMatches(pattern.getOperationPattern(), pap);
        boolean operandPatternsMatch = operandsMatch(pattern.getOperandPatterns(), pap);

        return opPatternMatches && operandPatternsMatch;
    }

    private boolean operationMatches(Pattern operationPattern, PAP pap) throws PMException {
        return operationPattern.matches(getOpName(), pap);
    }

    private boolean operandsMatch(List<Pattern> operandPatterns, PAP pap) throws PMException {
        // if there are more values provided than patterns, they cannot match
        // if there are more patterns than values than there still might be a match
        Object[] operands = getOperands();

        if (operands.length > operandPatterns.size()) {
            return false;
        }

        for (int i = 0; i < operands.length; i++) {
            Object operandValue = operands[i];
            Pattern operandPattern = operandPatterns.get(i);

            if (!operandPattern.matches(operandValue, pap)) {
                return false;
            }
        }

        return true;
    }
}
