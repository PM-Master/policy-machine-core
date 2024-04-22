package gov.nist.csd.pm.common.obligation.event.subject;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.List;

public class UserAttributesSubject extends Subject {

    public UserAttributesSubject(List<String> subjects) {
        super(subjects);
    }

    public UserAttributesSubject(String... subjects) {
        super(subjects);
    }

    @Override
    public boolean matches(UserContext userCtx, GraphReview graphReview) throws PMException {
        String user = userCtx.getUser();

        for (String subject : subjects) {
            if (graphReview.isContained(user, subject)) {
                return true;
            }
        }

        return false;
    }
}
