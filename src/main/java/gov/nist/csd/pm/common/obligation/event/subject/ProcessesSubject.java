package gov.nist.csd.pm.common.obligation.event.subject;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.List;

public class ProcessesSubject extends Subject {

    public ProcessesSubject(List<String> subjects) {
        super(subjects);
    }

    public ProcessesSubject(String... subjects) {
        super(subjects);
    }

    @Override
    public boolean matches(UserContext userCtx, GraphReview graphReview) throws PMException {
        return subjects.contains(userCtx.getProcess());
    }
}
