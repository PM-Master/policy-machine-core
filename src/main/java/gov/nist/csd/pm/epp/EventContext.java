package gov.nist.csd.pm.epp;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.event.EventPattern;
import gov.nist.csd.pm.common.obligation.event.subject.Subject;
import gov.nist.csd.pm.common.obligation.event.target.Target;
import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.common.op.Operation;

public class EventContext {

    private final UserContext userCtx;
    private final String eventName;
    private final String target;
    private final Operation event;

    public EventContext(UserContext userCtx, String target, Operation event) {
        this.userCtx = userCtx;
        this.eventName = event.getOpName();
        this.target = target;
        this.event = event;
    }

    public EventContext(UserContext userCtx, Operation event) {
        this.userCtx = userCtx;
        this.eventName = event.getOpName();
        this.target = "";
        this.event = event;
    }

    public UserContext getUserCtx() {
        return userCtx;
    }

    public String getEventName() {
        return eventName;
    }

    public String getTarget() {
        return target;
    }

    public Operation getEvent() {
        return event;
    }

    public boolean matchesPattern(EventPattern pattern, GraphReview graphReviewer) throws PMException {
        if (pattern.getOperations().isEmpty() || pattern.getOperations().get(0).isEmpty()) {
            return true; // an empty event pattern will match all events
        } else if (pattern.getOperations() != null &&
                !pattern.getOperations().contains(eventName)) {
            return false;
        }

        Subject patternSubject = pattern.getSubject();
        Target patternTarget = pattern.getTarget();

        return patternSubject.matches(userCtx, graphReviewer) &&
                patternTarget.matches(target, graphReviewer);
    }

    @Override
    public String toString() {
        return "EventContext{" +
                "userCtx=" + userCtx +
                ", eventName='" + eventName + '\'' +
                ", target=" + target +
                ", event=" + event +
                '}';
    }
}
