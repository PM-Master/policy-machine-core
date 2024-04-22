package gov.nist.csd.pm.common.obligation.event.subject;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.List;

public class UsersInUnionSubject extends Subject{
    public UsersInUnionSubject(List<String> users) {
        super(users);
    }

    @Override
    public boolean matches(UserContext userCtx, GraphReview graphReview) throws PMException {
        for (String subject : subjects) {
            if (graphReview.isContained(userCtx.getUser(), subject)) {
                return true;
            }
        }

        return false;
    }
}
