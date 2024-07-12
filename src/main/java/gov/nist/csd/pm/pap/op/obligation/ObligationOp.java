package gov.nist.csd.pm.pap.op.obligation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.common.pattern.Pattern;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Map;

public abstract class ObligationOp extends Operation<Void> {

    public static final String AUTHOR_OPERAND = "author";
    public static final String NAME_OPERAND = "name";
    public static final String RULES_OPERAND = "rules";

    public ObligationOp(String opName, String reqCap, OperationExecutor<Void> operationExecutor) {
        super(
                opName,
                Map.of(
                        AUTHOR_OPERAND, new RequiredCapability(),
                        NAME_OPERAND, new RequiredCapability(),
                        RULES_OPERAND, new RequiredCapability()
                ),
                (pap, userCtx, op, capMap, operands) -> {
                    List<Rule> rules = (List<Rule>) operands.get(RULES_OPERAND);
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
