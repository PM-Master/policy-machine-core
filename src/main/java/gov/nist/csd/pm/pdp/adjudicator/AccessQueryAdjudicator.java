package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.audit.Explain;
import gov.nist.csd.pm.pap.query.AccessQuery;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class AccessQueryAdjudicator implements AccessQuery {

    private final UserContext userCtx;
    private final PAP pap;

    public AccessQueryAdjudicator(UserContext userCtx, PAP pap) {
        this.userCtx = userCtx;
        this.pap = pap;
    }

    @Override
    public AccessRightSet computePrivileges(UserContext userCtx, String target) throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        PrivilegeChecker.check(pap, userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().computePrivileges(userCtx, target);
    }

    @Override
    public AccessRightSet computeDeniedPrivileges(UserContext userCtx, String target) throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        PrivilegeChecker.check(pap, userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().computeDeniedPrivileges(userCtx, target);
    }

    @Override
    public Map<String, AccessRightSet> computePolicyClassAccessRights(UserContext userCtx, String target)
            throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        PrivilegeChecker.check(pap, userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().computePolicyClassAccessRights(userCtx, target);
    }

    @Override
    public Map<String, AccessRightSet> buildCapabilityList(UserContext userCtx) throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().buildCapabilityList(userCtx);
    }

    @Override
    public Map<String, AccessRightSet> buildACL(String target) throws PMException {
        PrivilegeChecker.check(pap, userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().buildACL(target);
    }

    @Override
    public Map<String, AccessRightSet> findBorderAttributes(String user) throws PMException {
        PrivilegeChecker.check(pap, userCtx, user, AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().findBorderAttributes(user);
    }

    @Override
    public Map<String, AccessRightSet> computeSubgraphPrivileges(UserContext userCtx, String root) throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().computeSubgraphPrivileges(userCtx, root);
    }

    @Override
    public Explain explain(UserContext userCtx, String target) throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        PrivilegeChecker.check(pap, userCtx, target, AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().explain(userCtx, target);
    }

    @Override
    public Set<String> buildPOS(UserContext userCtx) throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().buildPOS(userCtx);
    }

    @Override
    public Collection<String> computeAccessibleChildren(UserContext userCtx, String root) throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        PrivilegeChecker.check(pap, userCtx, root, AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().computeAccessibleChildren(userCtx, root);
    }

    @Override
    public Collection<String> computeAccessibleParents(UserContext userCtx, String root) throws PMException {
        PrivilegeChecker.check(pap, userCtx, userCtx.getUser(), AdminAccessRights.REVIEW_POLICY);
        PrivilegeChecker.check(pap, userCtx, root, AdminAccessRights.REVIEW_POLICY);

        return pap.query().access().computeAccessibleParents(userCtx, root);
    }
}
