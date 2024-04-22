package gov.nist.csd.pm.common.obligation.event.subject;

import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.List;

public class UsersSubject extends Subject {

    public UsersSubject(List<String> subjects) {
        super(subjects);
    }

    public UsersSubject(String... subjects) {
        super(subjects);
    }

    @Override
    public boolean matches(UserContext userCtx, GraphReview graphReview) {
        return subjects.contains(userCtx.getUser());
    }
}
