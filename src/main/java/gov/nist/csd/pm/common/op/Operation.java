package gov.nist.csd.pm.common.op;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.event.EventPattern;
import gov.nist.csd.pm.pap.GraphReview;

import java.io.Serializable;

public interface Operation extends Serializable {

    /**
     * Get the name of this operation.
     * @return The name of the operation.
     */
    String getOpName();

    /**
     * Default implementation of matches only checks that the given operation pattern is either empty, which will match
     * all operations, or contains this Operation's name. It is expected an implementation of this method checks the
     * target pattern of the event pattern as well as calling Operation.super.matches() to ensure the operation matches.
     * @param pattern The event pattern.
     * @param graphReview A GraphReview object used to determine containment.
     * @return True if this Operation matches the given pattern, false otherwise.
     */
    default boolean matches(EventPattern pattern, GraphReview graphReview) throws PMException {
        return pattern.getOperations().isEmpty() || pattern.getOperations().contains(getOpName());
    }

    public  interface Pattern {

    }

}
