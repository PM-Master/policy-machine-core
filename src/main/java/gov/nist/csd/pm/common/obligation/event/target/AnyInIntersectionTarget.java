package gov.nist.csd.pm.common.obligation.event.target;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.List;

public class AnyInIntersectionTarget extends Target{

    public AnyInIntersectionTarget(List<String> targets) {
        super(targets);
    }

    public AnyInIntersectionTarget(String... targets) {
        super(targets);
    }

    @Override
    public boolean matches(String target, GraphReview graphReview) throws PMException {
        for (String container : getTargets()) {
            if (!graphReview.isContained(target, container)) {
                return false;
            }
        }

        return true;
    }
}
