package gov.nist.csd.pm.common.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.pdp.AccessRightSet;

import java.util.HashSet;
import java.util.Set;

public class Or extends Pattern<Object>  {

    public static Or or(Pattern<Object>... args) {
        return new Or(args);
    }

    private Pattern<Object>[] args;

    public Or(Pattern<Object>[] args) {
        this.args = args;
    }

    @Override
    public boolean matches(Object value, GraphReview graphReview) throws PMException {
        // evaluate all args, if one is false return false
        for (Pattern<Object> arg : args) {
            if (arg.matches(value, graphReview)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        ReferencedPolicyEntities ref = new ReferencedPolicyEntities();

        for (Pattern<Object> arg : args) {
            ReferencedPolicyEntities argReferencedPolicyEntities = arg.getReferencedPolicyEntities();

            if (argReferencedPolicyEntities.isAny()) {
                return new ReferencedPolicyEntities(new HashSet<>(), new AccessRightSet(), true);
            }

            ref.add(argReferencedPolicyEntities);
        }

        return ref;
    }

}
