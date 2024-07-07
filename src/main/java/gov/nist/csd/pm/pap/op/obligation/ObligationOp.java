package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.common.obligation.pattern.Pattern;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Collection;
import java.util.List;

public abstract class ObligationOp extends Operation<Void> {

    public ObligationOp(String opName, String reqCap, OperationExecutor<Void> operationExecutor) {
        super(
                opName,
                List.of(
                        new RequiredCapability("author"),
                        new RequiredCapability("name"),
                        new RequiredCapability("rules")
                ),
                (pap, userCtx, op, capMap, operands) -> {
                    List<Rule> rules = (List<Rule>) operands.get(2);
                    for (Rule rule : rules) {
                        EventPattern eventPattern = rule.getEventPattern();

                        // check subject pattern
                        Pattern pattern = eventPattern.getSubjectPattern();
                        checkPatternPrivileges(pap, userCtx, pattern, AdminPolicyNode.OBLIGATIONS_TARGET, reqCap);

                        // check operand patterns
                        for (Pattern operandPattern : eventPattern.getOperandPatterns()) {
                            checkPatternPrivileges(pap, userCtx, operandPattern, AdminPolicyNode.OBLIGATIONS_TARGET, reqCap);
                        }
                    }
                },
                operationExecutor
        );
    }


    protected static void checkPatternPrivileges(PAP pap, UserContext userCtx, Pattern pattern, AdminPolicyNode target, String toCheck) throws PMException {
        ReferencedNodes referencedNodes = pattern.getReferencedNodes();
        if (referencedNodes.isAny()) {
            PrivilegeChecker.check(pap, userCtx, target.nodeName(), toCheck);

            return;
        }

        for (String entity : referencedNodes.nodes()) {
            PrivilegeChecker.check(pap, userCtx, entity, toCheck);
        }
    }
}
