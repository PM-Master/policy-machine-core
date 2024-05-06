package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pdp.UserContext;

import java.util.List;

public class PMLEventPattern extends EventPattern {

    public PMLEventPattern(PMLPattern<String> subjectPattern,
                           PMLPattern<String> operationPattern,
                           List<Pattern<Object>> operandPatterns) {
        super(subjectPattern, operationPattern, operandPatterns);
    }

    @Override
    public boolean userMatches(UserContext userCtx, PAP pap) throws PMException {
        ExecutionContext executionContext = new ExecutionContext(null, GlobalScope.forExecute(pap.policy()));
        PMLPattern<String> pmlPattern = (PMLPattern<String>) subjectPattern;
        executionContext.scope().local().addOrOverwriteVariable(
                pmlPattern.getVarName(),
                new StringValue(userCtx.getUser())
        );

        return pmlPattern.getPatternExpr().execute(executionContext, pap.policy()).getBooleanValue();
    }

    @Override
    public boolean processMatches(UserContext userCtx, PAP pap) throws PMException {
        ExecutionContext executionContext = new ExecutionContext(null, GlobalScope.forExecute(pap.policy()));
        PMLPattern<String> pmlPattern = (PMLPattern<String>) subjectPattern;
        executionContext.scope().local().addOrOverwriteVariable(
                pmlPattern.getVarName(),
                new StringValue(userCtx.getProcess())
        );

        return pmlPattern.getPatternExpr().execute(executionContext, pap.policy()).getBooleanValue();
    }

    @Override
    public boolean operationMatches(Operation op, PAP pap) throws PMException {
        ExecutionContext executionContext = new ExecutionContext(null, GlobalScope.forExecute(pap.policy()));
        PMLPattern<String> pmlPattern = (PMLPattern<String>) operationPattern;
        executionContext.scope().local().addOrOverwriteVariable(
                pmlPattern.getVarName(),
                new StringValue(op.getOpName())
        );

        return pmlPattern.getPatternExpr().execute(executionContext, pap.policy()).getBooleanValue();
    }

    @Override
    public boolean operandsMatch(Object[] operands, PAP pap) throws PMException {
        for (int i = 0; i < operands.length; i++) {
            // if there are more operands than patterns return false
            if (i >= operandPatterns.size()) {
                return false;
            }

            Object operand = operands[i];
            Pattern<Object> pattern = operandPatterns.get(i);
            PMLPattern<Object> pmlPattern = (PMLPattern<Object>) pattern;

            // create a new PML execution context and add the operand value to the local scope
            ExecutionContext executionContext = new ExecutionContext(null, GlobalScope.forExecute(pap.policy()));
            executionContext.scope().local().addOrOverwriteVariable(pmlPattern.getVarName(), Value.fromObject(operand));

            if (!pmlPattern.getPatternExpr().execute(executionContext, pap.policy()).getBooleanValue()) {
                return false;
            }
        }

        return true;
    }
}
