package gov.nist.csd.pm.common.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.Graph;
import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.pdp.AdminAccessRights;

public abstract class Pattern<T> {

    public abstract boolean matches(T value, GraphReview graphReview) throws PMException;

    public abstract ReferencedPolicyEntities getReferencedPolicyEntities();

    public boolean checkReferencedPolicyEntitiesExist(Graph graph) throws PMException {
        ReferencedPolicyEntities ref = getReferencedPolicyEntities();

        // check nodes
        for (String node : ref.nodes()) {
            if (!graph.nodeExists(node)) {
                return false;
            }
        }

        // check access rights
        for (String ar : ref.ars()) {
            if (!graph.getResourceAccessRights().contains(ar) && !AdminAccessRights.isAdminAccessRight(ar)) {
                return false;
            }
        }

        // TODO check access right sets

        // TODO check operations

        return true;
    }
}
