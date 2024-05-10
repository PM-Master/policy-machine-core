package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.audit.Explain;
import gov.nist.csd.pm.pap.query.AccessQuery;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdjudicatorAccessQuery implements AccessQuery {

    private final UserContext userCtx;
    private final PrivilegeChecker privilegeChecker;

    public AdjudicatorAccessQuery(UserContext userCtx, PrivilegeChecker privilegeChecker) {
        this.userCtx = userCtx;
        this.privilegeChecker = privilegeChecker;
    }

    @Override
    public AccessRightSet computePrivileges(UserContext userCtx, String target) throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        privilegeChecker.check(this.userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public AccessRightSet computeDeniedPrivileges(UserContext userCtx, String target) throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        privilegeChecker.check(this.userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public Map<String, AccessRightSet> computePolicyClassAccessRights(UserContext userCtx, String target)
            throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        privilegeChecker.check(this.userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public Map<String, AccessRightSet> buildCapabilityList(UserContext userCtx) throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public Map<String, AccessRightSet> buildACL(String target) throws PMException {
        privilegeChecker.check(this.userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public Map<String, AccessRightSet> findBorderAttributes(String user) throws PMException {
        privilegeChecker.check(this.userCtx, user, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public Map<String, AccessRightSet> computeSubgraphPrivileges(UserContext userCtx, String root) throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public Explain explain(UserContext userCtx, String target) throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        privilegeChecker.check(this.userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public Set<String> buildPOS(UserContext userCtx) throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public List<String> computeAccessibleChildren(UserContext userCtx, String root) throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        privilegeChecker.check(this.userCtx, root, AdminAccessRights.REVIEW_POLICY);

        return null;
    }

    @Override
    public List<String> computeAccessibleParents(UserContext userCtx, String root) throws PMException {
        privilegeChecker.check(this.userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        privilegeChecker.check(this.userCtx, root, AdminAccessRights.REVIEW_POLICY);

        return null;
    }
}
