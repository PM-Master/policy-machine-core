package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.query.GraphQuery;

import java.util.List;

public class AdjudicatorGraphQuery implements GraphQuery {

    private final UserContext userCtx;
    private final PrivilegeChecker privilegeChecker;

    public AdjudicatorGraphQuery(UserContext userCtx, PrivilegeChecker privilegeChecker) {
        this.userCtx = userCtx;
        this.privilegeChecker = privilegeChecker;
    }

    @Override
    public List<String> getAttributeContainers(String node) throws PMException {
        privilegeChecker.check(this.userCtx, node, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public List<String> getPolicyClassContainers(String node) throws PMException {
        privilegeChecker.check(this.userCtx, node, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public boolean isContained(String subject, String container) throws PMException {
        privilegeChecker.check(this.userCtx, subject, AdminAccessRights.REVIEW_POLICY);
        privilegeChecker.check(this.userCtx, container, AdminAccessRights.REVIEW_POLICY);

        return false;
    }
}
