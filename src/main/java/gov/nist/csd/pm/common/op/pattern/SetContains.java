package gov.nist.csd.pm.common.op.pattern;

import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.pdp.AccessRightSet;

import java.util.Set;

public class SetContains extends Pattern<Set> {

    public static SetContains setContains(Object arg) {
        return new SetContains(arg);
    }

    private Object arg;

    public SetContains(Object arg) {
        this.arg = arg;
    }

    @Override
    public boolean matches(Set value, GraphReview graphReview) {
        return value.contains(arg);
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        return new ReferencedPolicyEntities(Set.of(String.valueOf(arg)), new AccessRightSet(), false);
    }

}