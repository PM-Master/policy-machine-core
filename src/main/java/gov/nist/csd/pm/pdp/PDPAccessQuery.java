package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pdp.adjudicator.AdjudicatorAccessQuery;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.audit.Explain;
import gov.nist.csd.pm.pap.query.AccessQuery;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PDPAccessQuery implements AccessQuery {

    private final AdjudicatorAccessQuery adjudicator;
    private final AccessQuery accessQuery;

    public PDPAccessQuery(AdjudicatorAccessQuery adjudicator, AccessQuery accessQuery) {
        this.adjudicator = adjudicator;
        this.accessQuery = accessQuery;
    }

    @Override
    public AccessRightSet computePrivileges(UserContext userCtx, String target) throws PMException {
        adjudicator.computePrivileges(userCtx, target);
        return accessQuery.computePrivileges(userCtx, target);
    }

    @Override
    public AccessRightSet computeDeniedPrivileges(UserContext userCtx, String target) throws PMException {
        adjudicator.computeDeniedPrivileges(userCtx, target);
        return accessQuery.computeDeniedPrivileges(userCtx, target);
    }

    @Override
    public Map<String, AccessRightSet> computePolicyClassAccessRights(UserContext userCtx, String target)
            throws PMException {
        adjudicator.computePolicyClassAccessRights(userCtx, target);
        return accessQuery.computePolicyClassAccessRights(userCtx, target);
    }

    @Override
    public Map<String, AccessRightSet> buildCapabilityList(UserContext userCtx) throws PMException {
        adjudicator.buildCapabilityList(userCtx);
        return accessQuery.buildCapabilityList(userCtx);
    }

    @Override
    public Map<String, AccessRightSet> buildACL(String target) throws PMException {
        adjudicator.buildACL(target);
        return accessQuery.buildACL(target);
    }

    @Override
    public Map<String, AccessRightSet> findBorderAttributes(String user) throws PMException {
        adjudicator.findBorderAttributes(user);
        return accessQuery.findBorderAttributes(user);
    }

    @Override
    public Map<String, AccessRightSet> computeSubgraphPrivileges(UserContext userCtx, String root) throws PMException {
        adjudicator.computeSubgraphPrivileges(userCtx, root);
        return accessQuery.computeSubgraphPrivileges(userCtx, root);
    }

    @Override
    public Explain explain(UserContext userCtx, String target) throws PMException {
        adjudicator.explain(userCtx, target);
        return accessQuery.explain(userCtx, target);
    }

    @Override
    public Set<String> buildPOS(UserContext userCtx) throws PMException {
        adjudicator.buildPOS(userCtx);
        return accessQuery.buildPOS(userCtx);
    }

    @Override
    public List<String> computeAccessibleChildren(UserContext userCtx, String root) throws PMException {
        adjudicator.computeAccessibleChildren(userCtx, root);
        return accessQuery.computeAccessibleChildren(userCtx, root);
    }

    @Override
    public List<String> computeAccessibleParents(UserContext userCtx, String root) throws PMException {
        adjudicator.computeAccessibleParents(userCtx, root);
        return accessQuery.computeAccessibleParents(userCtx, root);
    }
}
