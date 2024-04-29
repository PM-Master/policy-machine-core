package gov.nist.csd.pm.common.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.event.EventPattern;
import gov.nist.csd.pm.common.obligation.event.target.Target;
import gov.nist.csd.pm.common.op.Operation;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.Objects;

public class AssignOp implements Operation {
    private final String child;
    private final String parent;

    public AssignOp(String child, String parent) {
        this.child = child;
        this.parent = parent;
    }

    @Override
    public String getOpName() {
        return "assign";
    }

    @Override
    public boolean matches(EventPattern pattern, GraphReview graphReview) throws PMException {
        /*if (pattern instanceof Pattern) {

        }*/

        boolean opPatternMatches = Operation.super.matches(pattern, graphReview);

        Target target = pattern.getTarget();
        boolean targetPatternMatches = target.matches(child, graphReview) || target.matches(parent, graphReview);

        return opPatternMatches && targetPatternMatches;
    }

    public class Pattern implements Operation.Pattern {

    }

    public String child() {
        return child;
    }

    public String parent() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (AssignOp) obj;
        return Objects.equals(this.child, that.child) &&
                Objects.equals(this.parent, that.parent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(child, parent);
    }

    @Override
    public String toString() {
        return "AssignOp[" +
                "child=" + child + ", " +
                "parent=" + parent + ']';
    }
}
