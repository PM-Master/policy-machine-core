package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.exception.InvalidOperationException;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.op.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.query.UserContext;

import java.io.Serializable;
import java.util.*;

public abstract class Operation<T> implements Serializable {

    protected String opName;
    protected List<RequiredCapability> capMap;
    protected List<Object> operands;

    public Operation(String opName, List<RequiredCapability> capMap) {
        this.opName = opName;
        this.capMap = capMap;
    }

    public Operation(String opName, List<RequiredCapability> capMap, List<Object> operands) {
        this.opName = opName;
        this.capMap = capMap;
        this.operands = operands;
    }

    public abstract T execute(PAP pap) throws PMException;

    public Operation<T> canExecute(PAP pap, UserContext userCtx) throws PMException {
        if (operands.size() != capMap.size()) {
            throw new InvalidOperationException(opName, capMap, operands);
        }

        for (int i = 0; i < capMap.size(); i++) {
            RequiredCapability reqCap = capMap.get(i);

            // skip operands that do not have a required capability
            if (reqCap.caps().isEmpty()) {
                continue;
            }

            Object operand = operands.get(i);

            // policy element operands can be strings or collections of strings
            if (operand instanceof String strOp) {
                PrivilegeChecker.check(pap, userCtx, strOp, reqCap.capsArray());
            } else {
                if (operand instanceof Collection<?> colOp) {
                    for (Object o : colOp) {
                        if (o instanceof String strColOp) {
                            PrivilegeChecker.check(pap, userCtx, strColOp, reqCap.capsArray());
                        }
                    }
                }
            }
        }

        return this;
    }

    public EventContext execute(PAP pap, UserContext userCtx) throws PMException {
        canExecute(pap, userCtx);

        execute(pap);

        return toEventContext(userCtx, operands);
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public List<RequiredCapability> getCapMap() {
        return capMap;
    }

    public void setCapMap(List<RequiredCapability> capMap) {
        this.capMap = capMap;
    }

    public List<Object> getOperands() {
        return operands;
    }

    public void setOperands(List<Object> operands) {
        this.operands = operands;
    }


    public void setOperands(Object ... operands) {
        setOperands(List.of(operands));
    }

    protected void checkPatternPrivileges(PAP pap, UserContext userCtx, Pattern pattern, AdminPolicyNode target, String toCheck) throws PMException {
        ReferencedNodes referencedNodes = pattern.getReferencedNodes();
        if (referencedNodes.isAny()) {
            PrivilegeChecker.check(pap, userCtx, target.nodeName(), toCheck);

            return;
        }

        for (String entity : referencedNodes.nodes()) {
            PrivilegeChecker.check(pap, userCtx, entity, toCheck);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Operation operation = (Operation) o;
        return Objects.equals(opName, operation.opName) && Objects.equals(capMap, operation.capMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opName, capMap);
    }

    private EventContext toEventContext(UserContext userCtx, Object ... operands) {
        HashMap<String, Object> operandsMap = new HashMap<>();

        for (int i = 0; i < operands.length; i++) {
            operandsMap.put(capMap.get(i).operand(), operands[i]);
        }

        return new EventContext(userCtx, opName, operandsMap);
    }
}
