package gov.nist.csd.pm.pap.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.PolicyQuery;

public class Any extends Pattern<Object> {

    public static Any any() {
        return new Any();
    }

    @Override
    public boolean matches(Object value, PolicyQuery querier) throws PMException {
        return true;
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        return new ReferencedPolicyEntities(true);
    }

}
