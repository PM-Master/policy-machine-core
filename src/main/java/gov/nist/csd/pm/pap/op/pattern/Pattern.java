package gov.nist.csd.pm.pap.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.pml.pattern.PatternExpression;
import gov.nist.csd.pm.pap.query.PolicyQuery;

public abstract class Pattern {

    public abstract boolean matches(Object value, PAP pap) throws PMException;

    public abstract ReferencedPolicyEntities getReferencedPolicyEntities();

    public abstract PatternExpression toPatternExpression();

    public void checkReferencedPolicyEntitiesExist(PolicyQuery querier) throws PMException {
        ReferencedPolicyEntities ref = getReferencedPolicyEntities();

        for (String entity : ref.entities()) {
            boolean ok = false;

            if (!querier.graph().nodeExists(entity)) {
                ok = true;
            } else if (!querier.graph().getResourceAccessRights().contains(entity) &&
                    !AdminAccessRights.isAdminAccessRight(entity)) {
                ok = true;
            }

            // TODO check access right sets

            // TODO check operations

            if (!ok) {
                throw new ReferencedPolicyEntityDoesNotExistException(entity);
            }
        }
    }
}
