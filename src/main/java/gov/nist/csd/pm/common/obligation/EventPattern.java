package gov.nist.csd.pm.common.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.pattern.Pattern;
import gov.nist.csd.pm.pap.PAP;

import java.io.Serializable;
import java.util.*;

public class EventPattern implements Serializable {

    protected Pattern subjectPattern;
    protected List<String> operationPattern;
    protected List<Pattern> operandPatterns;

    public EventPattern(Pattern subjectPattern,
                        List<String> operationPattern,
                        List<Pattern> operandPatterns) {
        this.subjectPattern = subjectPattern;
        this.operationPattern = operationPattern;
        this.operandPatterns = operandPatterns;
    }

    public EventPattern(Pattern subjectPattern, List<String> operationPattern) {
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

    public List<String> getOperationPattern() {
        return operationPattern;
    }

    public void setOperationPattern(List<String> operationPattern) {
        this.operationPattern = operationPattern;
    }

    public List<Pattern> getOperandPatterns() {
        return operandPatterns;
    }

    public void setOperandPatterns(List<Pattern> operandPatterns) {
        this.operandPatterns = operandPatterns;
    }

    public boolean userMatches(String user, PAP pap) throws PMException {
        return subjectPattern.matches(user, pap);
    }

    public boolean processMatches(String process, PAP pap) throws PMException {
        return subjectPattern.matches(process, pap);
    }

    public boolean operationMatches(String opName) throws PMException {
        return operationPattern.contains(opName);
    }

    public boolean operandsMatch(List<Object> operands, PAP pap) throws PMException {
        // if more patterns than operands - false
        // if no patterns - true (match everything)
        if (operandPatterns.size() > operands.size()) {
            return false;
        } else if (operandPatterns.isEmpty()) {
            return true;
        }

        for (int i = 0; i < operandPatterns.size(); i++) {
            Object operand = operands.get(i);
            Pattern pattern = operandPatterns.get(i);
            if (!pattern.matches(operand, pap)) {
                return false;
            }
        }

        return true;
    }

    public boolean matches(EventContext eventCtx, PAP pap) throws PMException {
        boolean userMatches = userMatches(eventCtx.user(), pap);
        boolean processMatches = processMatches(eventCtx.process(), pap);
        boolean opMatches = operationMatches(eventCtx.opName());
        boolean operandsMatch = operandsMatch(eventCtx.operands(), pap);

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
