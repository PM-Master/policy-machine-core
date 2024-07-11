package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Objects;

public class PreparedOperation<T> implements OperationPrivilegeChecker {

    private Operation<T> op;
    private List<Object> operands;

    public PreparedOperation(Operation<T> op, List<Object> operands) {
        this.op = op;
        this.operands = operands;
    }

    public Operation<T> getOp() {
        return op;
    }

    public void setOp(Operation<T> op) {
        this.op = op;
    }

    public List<Object> getOperands() {
        return operands;
    }

    public void setOperands(List<Object> operands) {
        this.operands = operands;
    }

    @Override
    public void canExecute(PAP pap, UserContext userCtx, String opName, List<RequiredCapability> capMap,
                           List<Object> operands) throws PMException {
        op.checker.canExecute(pap, userCtx, opName, capMap, operands);
    }

    public T execute(PAP pap) throws PMException {
        return op.executor.execute(pap, operands);
    }

    public EventContext execute(PAP pap, UserContext userCtx) throws PMException {
        canExecute(pap, userCtx, op.getName(), op.getCapMap(), operands);
        execute(pap);

        return new EventContext(userCtx.getUser(), userCtx.getProcess(), op.getName(), List.of(operands));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PreparedOperation<?> that = (PreparedOperation<?>) o;
        return Objects.equals(op, that.op) && Objects.equals(operands, that.operands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, operands);
    }
}
