package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.audit.Explain;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface AccessQuery {

    AccessRightSet computePrivileges(UserContext userCtx, String target) throws PMException;
    AccessRightSet computeDeniedPrivileges(UserContext userCtx, String target) throws PMException;
    Map<String, AccessRightSet> computePolicyClassAccessRights(UserContext userContext, String target) throws PMException;
    Map<String, AccessRightSet> buildCapabilityList(UserContext userCtx) throws PMException;
    Map<String, AccessRightSet> buildACL(String target) throws PMException;
    Map<String, AccessRightSet> findBorderAttributes(String user) throws PMException;
    Map<String, AccessRightSet> computeSubgraphPrivileges(UserContext userCtx, String root) throws PMException;
    Explain explain(UserContext userCtx, String target) throws PMException;
    Set<String> buildPOS(UserContext userCtx) throws PMException;
    Collection<String> computeAccessibleChildren(UserContext userCtx, String root) throws PMException;
    Collection<String> computeAccessibleParents(UserContext userCtx, String root) throws PMException;

}
