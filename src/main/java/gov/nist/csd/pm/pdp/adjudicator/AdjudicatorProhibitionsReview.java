package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.AdminAccessRights;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.pap.ProhibitionsReview;

import java.util.List;

public class AdjudicatorProhibitionsReview implements ProhibitionsReview {

    private final UserContext userCtx;
    private final PrivilegeChecker privilegeChecker;

    public AdjudicatorProhibitionsReview(UserContext userCtx, PrivilegeChecker privilegeChecker) {
        this.userCtx = userCtx;
        this.privilegeChecker = privilegeChecker;
    }

    @Override
    public List<Prohibition> getInheritedProhibitionsFor(String subject) throws PMException {
        privilegeChecker.check(this.userCtx, subject, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public List<Prohibition> getProhibitionsWithContainer(String container) throws PMException {
        privilegeChecker.check(this.userCtx, container, AdminAccessRights.REVIEW_POLICY);

        return null;
    }
}
