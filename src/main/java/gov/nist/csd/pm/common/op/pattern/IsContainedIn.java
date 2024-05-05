package gov.nist.csd.pm.common.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.pdp.AccessRightSet;

import java.util.Set;

public class IsContainedIn extends Pattern<String> {

    public static IsContainedIn isContainedIn(String arg) {
        return new IsContainedIn(arg);
    }

    private String arg;

    public IsContainedIn(String arg) {
        this.arg = arg;
    }

    @Override
    public boolean matches(String value, GraphReview graphReview) throws PMException {
        return graphReview.isContained(value, arg);
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        return new ReferencedPolicyEntities(Set.of(String.valueOf(arg)), new AccessRightSet(), false);
    }

}
