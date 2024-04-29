package gov.nist.csd.pm.epp;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.event.EventPattern;
import gov.nist.csd.pm.common.obligation.event.subject.Subject;
import gov.nist.csd.pm.common.obligation.event.target.Target;
import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.common.op.Operation;

import java.util.Objects;

public class EventContext {

    private final UserContext userCtx;
    private final Operation op;

    public EventContext(UserContext userCtx, Operation op) {
        this.userCtx = userCtx;
        this.op = op;
    }

    public UserContext getUserCtx() {
        return userCtx;
    }

    public Operation getOp() {
        return op;
    }

    public boolean matchesPattern(EventPattern pattern, GraphReview graphReviewer) throws PMException {
        // check that the user context matches the user pattern and the op matches the op pattern - this will check targets
        return pattern.getSubject().matches(userCtx, graphReviewer) && op.matches(pattern, graphReviewer);
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
