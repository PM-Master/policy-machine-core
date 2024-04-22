package gov.nist.csd.pm.common.obligation.event.subject;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.List;

public class UsersInIntersectionSubject extends Subject{
    public UsersInIntersectionSubject(List<String> users) {
        super(users);
    }

    @Override
    public boolean matches(UserContext userCtx, GraphReview graphReview) throws PMException {
        for (String subject : subjects) {
            if (!graphReview.isContained(userCtx.getUser(), subject)) {
                return false;
            }
        }

        return true;
    }
}
