package gov.nist.csd.pm.pap.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.PolicyQuery;
import gov.nist.csd.pm.pap.op.AdminAccessRights;

public abstract class Pattern<T> {

    public abstract boolean matches(T value, PolicyQuery querier) throws PMException;

    public abstract ReferencedPolicyEntities getReferencedPolicyEntities();

    public boolean checkReferencedPolicyEntitiesExist(PolicyQuery query) throws PMException {
        ReferencedPolicyEntities ref = getReferencedPolicyEntities();

        // check nodes
        for (String node : ref.nodes()) {
            if (!query.graph().nodeExists(node)) {
                return false;
            }
        }

        // check access rights
        for (String ar : ref.ars()) {
            if (!query.graph().getResourceAccessRights().contains(ar) && !AdminAccessRights.isAdminAccessRight(ar)) {
                return false;
            }
        }

        // TODO check access right sets

        // TODO check operations

        return true;
    }
}
