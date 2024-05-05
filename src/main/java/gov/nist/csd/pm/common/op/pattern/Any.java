package gov.nist.csd.pm.common.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.GraphReview;

import java.util.HashSet;

public class Any extends Pattern<Object> {

    public static Any any() {
        return new Any();
    }

    @Override
    public boolean matches(Object value, GraphReview graphReview) throws PMException {
        return true;
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        return new ReferencedPolicyEntities(true);
    }

}
