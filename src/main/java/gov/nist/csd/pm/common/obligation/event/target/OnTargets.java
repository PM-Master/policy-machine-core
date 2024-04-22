package gov.nist.csd.pm.common.obligation.event.target;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.List;

public class OnTargets extends Target{

    public OnTargets(List<String> targets) {
        super(targets);
    }

    public OnTargets(String... targets) {
        super(targets);
    }

    @Override
    public boolean matches(String target, GraphReview graphReview) throws PMException {
        return targets.contains(target);
    }
}
