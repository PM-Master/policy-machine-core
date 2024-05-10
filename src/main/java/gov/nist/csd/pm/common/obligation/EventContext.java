package gov.nist.csd.pm.common.obligation;

import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.Objects;

public class EventContext {

    private final UserContext userCtx;
    private final String opName;
    private final Operation op;

    public EventContext(UserContext userCtx, Operation op) {
        this.userCtx = userCtx;
        this.opName = op.getOpName();
        this.op = op;
    }

    public UserContext getUserCtx() {
        return userCtx;
    }

    public String getOpName() {
        return opName;
    }

    public Operation getOp() {
        return op;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventContext that = (EventContext) o;
        return Objects.equals(userCtx, that.userCtx) && Objects.equals(op, that.op);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userCtx, op);
    }

    @Override
    public String toString() {
        return "EventContext{" +
                "userCtx=" + userCtx +
                ", op=" + op +
                '}';
    }
}
