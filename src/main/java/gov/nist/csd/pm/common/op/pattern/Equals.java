package gov.nist.csd.pm.common.op.pattern;

import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.pdp.AccessRightSet;

import java.util.Set;

public class Equals extends Pattern<Object> {

    public static Equals equals(Pattern<Object>... args) {
        return new Equals(args);
    }

    private Object arg;

    public Equals(Object arg) {
        this.arg = arg;
    }

    @Override
    public boolean matches(Object value, GraphReview graphReview) {
        return value.equals(arg);
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        return new ReferencedPolicyEntities(Set.of(String.valueOf(arg)), new AccessRightSet(), false);
    }

}
