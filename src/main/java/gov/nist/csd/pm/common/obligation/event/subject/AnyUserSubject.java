package gov.nist.csd.pm.common.obligation.event.subject;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.ArrayList;

public class AnyUserSubject extends Subject {
    public AnyUserSubject() {
        super(new ArrayList<>());
    }

    @Override
    public boolean matches(UserContext userCtx, GraphReview graphReview) throws PMException {
        return true;
    }
}
