package gov.nist.csd.pm.common.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;

import java.io.Serializable;
import java.util.*;

public class EventPattern implements Serializable {

    protected Pattern subjectPattern;
    protected Pattern operationPattern;
    protected List<Pattern> operandPatterns;

    public EventPattern(Pattern subjectPattern,
                        Pattern operationPattern,
                        List<Pattern> operandPatterns) {
        this.subjectPattern = subjectPattern;
        this.operationPattern = operationPattern;
        this.operandPatterns = operandPatterns;
    }

    public EventPattern(Pattern subjectPattern, Pattern operationPattern) {
        this.subjectPattern = subjectPattern;
        this.operationPattern = operationPattern;
        this.operandPatterns = new ArrayList<>();
    }

    public EventPattern() {
    }

    public Pattern getSubjectPattern() {
        return subjectPattern;
    }

    public void setSubjectPattern(Pattern subjectPattern) {
        this.subjectPattern = subjectPattern;
    }

    public Pattern getOperationPattern() {
        return operationPattern;
    }

    public void setOperationPattern(Pattern operationPattern) {
        this.operationPattern = operationPattern;
    }

    public List<Pattern> getOperandPatterns() {
        return operandPatterns;
    }

    public void setOperandPatterns(List<Pattern> operandPatterns) {
        this.operandPatterns = operandPatterns;
    }

    public boolean userMatches(UserContext userCtx, PAP pap) throws PMException {
        return subjectPattern.matches(userCtx.getUser(), pap);
    }

    public boolean processMatches(UserContext userCtx, PAP pap) throws PMException {
        return subjectPattern.matches(userCtx.getProcess(), pap);
    }

    public boolean operationMatches(Operation op, PAP pap) throws PMException {
        return operationPattern.matches(op.getOpName(), pap);
    }

    public boolean operandsMatch(Object[] operands, PAP pap) throws PMException {
        // if more patterns than operands - false
        // if no patterns - true (match everything)
        if (operandPatterns.size() > operands.length) {
            return false;
        } else if (operandPatterns.isEmpty()) {
            return true;
        }

        for (int i = 0; i < operandPatterns.size(); i++) {
            Object operand = operands[i];
            Pattern pattern = operandPatterns.get(i);
            if (!pattern.matches(operand, pap)) {
                return false;
            }
        }

        return true;
    }

    public boolean matches(EventContext eventCtx, PAP pap) throws PMException {
        boolean userMatches = userMatches(eventCtx.getUserCtx(), pap);
        boolean processMatches = processMatches(eventCtx.getUserCtx(), pap);
        boolean opMatches = operationMatches(eventCtx.getOp(), pap);
        boolean operandsMatch = operandsMatch(eventCtx.getOp().getOperands(), pap);

        return (userMatches || processMatches) && opMatches && operandsMatch;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (EventPattern) obj;

        return Objects.equals(this.subjectPattern, that.subjectPattern) &&
                Objects.equals(this.operationPattern, that.operationPattern) &&
                Objects.equals(this.operandPatterns, that.operandPatterns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectPattern, operationPattern, operandPatterns);
    }

    @Override
    public String toString() {
        return "EventPattern[" +
                "subjectPattern=" + subjectPattern + ", " +
                "operationPattern=" + operationPattern + ", " +
                "operandPatterns=" + operandPatterns + ']';
    }


}
