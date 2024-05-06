package gov.nist.csd.pm.common.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.UserContext;

import java.io.Serializable;
import java.util.*;

public class EventPattern implements Serializable {

    protected Pattern<String> subjectPattern;
    protected Pattern<String> operationPattern;
    protected List<Pattern<Object>> operandPatterns;

    public EventPattern(Pattern<String> subjectPattern,
                        Pattern<String> operationPattern,
                        List<Pattern<Object>> operandPatterns) {
        this.subjectPattern = subjectPattern;
        this.operationPattern = operationPattern;
        this.operandPatterns = operandPatterns;
    }

    public EventPattern() {
    }

    public Pattern<String> getSubjectPattern() {
        return subjectPattern;
    }

    public void setSubjectPattern(Pattern<String> subjectPattern) {
        this.subjectPattern = subjectPattern;
    }

    public Pattern<String> getOperationPattern() {
        return operationPattern;
    }

    public void setOperationPattern(Pattern<String> operationPattern) {
        this.operationPattern = operationPattern;
    }

    public List<Pattern<Object>> getOperandPatterns() {
        return operandPatterns;
    }

    public void setOperandPatterns(List<Pattern<Object>> operandPatterns) {
        this.operandPatterns = operandPatterns;
    }

    public boolean userMatches(UserContext userCtx, PAP pap) throws PMException {
        return subjectPattern.matches(userCtx.getUser(), pap.review().graph());
    }

    public boolean processMatches(UserContext userCtx, PAP pap) throws PMException {
        return subjectPattern.matches(userCtx.getProcess(), pap.review().graph());
    }

    public boolean operationMatches(Operation op, PAP pap) throws PMException {
        return operationPattern.matches(op.getOpName(), pap.review().graph());
    }

    public boolean operandsMatch(Object[] operands, PAP pap) throws PMException {
        for (int i = 0; i < operands.length; i++) {
            // if there are more operands than patterns return false
            if (i >= operandPatterns.size()) {
                return false;
            }

            Object operand = operands[i];
            Pattern<Object> pattern = operandPatterns.get(i);
            if (pattern.matches(operand, pap.review().graph())) {
                return false;
            }
        }

        return true;
    }

    public boolean matches(EventContext eventCtx, PAP pap) throws PMException {
        // subject
        boolean userMatches = userMatches(eventCtx.getUserCtx(), pap);
        boolean processMatches = processMatches(eventCtx.getUserCtx(), pap);

        // operation
        boolean opMatches = operationMatches(eventCtx.getOp(), pap);

        // operands
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
