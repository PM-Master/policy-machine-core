package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.operand.PolicyElementListOperand;
import gov.nist.csd.pm.pap.op.operand.PolicyElementOperand;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.op.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pdp.exception.UnauthorizedException;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static gov.nist.csd.pm.common.graph.node.NodeType.PC;

public abstract class Operation implements Serializable {

    protected transient String opName;
    protected transient List<Operand> operands;

    public Operation(String opName, Operand ... operands) {
        this.opName = opName;
        this.operands = List.of(operands);
    }
    public abstract void execute(PAP pap) throws PMException;
    public abstract void canExecute(PAP pap, UserContext userCtx) throws PMException;

    public String getOpName() {
        return opName;
    }

    public List<Operand> getOperands() {
        return operands;
    }

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
        List<Operand> operands = getOperands();

        if (operands.size() > operandPatterns.size()) {
            return false;
        }

        for (int i = 0; i < operands.size(); i++) {
            Object operandValue = operands.get(i);
            Pattern operandPattern = operandPatterns.get(i);

            if (!operandPattern.matches(operandValue, pap)) {
                return false;
            }
        }

        return true;
    }

    protected void checkPrivilegesOnListOperand(PAP pap, UserContext userCtx, PolicyElementListOperand operand) throws PMException {
        Collection<String> value = operand.getValue();

        for (String v : value) {
            PrivilegeChecker.check(pap, userCtx, v, operand.getReqCap());
        }
    }

    protected void checkPrivilegesOnOperand(PAP pap, UserContext userCtx, PolicyElementOperand operand) throws PMException {
        PrivilegeChecker.check(pap, userCtx, operand.getName(), operand.getValue());
    }

    protected void checkPrivilegesOnAdminNode(PAP pap, UserContext userCtx, AdminPolicyNode node, String ar) throws PMException {
        String target = node.nodeName();

        PrivilegeChecker.check(pap, userCtx, target, ar);
    }

    protected void checkPatternPrivileges(PAP pap, UserContext userCtx, Pattern pattern, AdminPolicyNode target, String toCheck) throws PMException {
        ReferencedNodes referencedNodes = pattern.getReferencedNodes();
        if (referencedNodes.isAny()) {
            checkPrivilegesOnAdminNode(pap, userCtx, target, toCheck);

            return;
        }

        for (String entity : referencedNodes.nodes()) {
            PrivilegeChecker.check(pap, userCtx, entity, toCheck);
        }

    }
}
