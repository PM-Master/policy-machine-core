package gov.nist.csd.pm.common.op.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.pdp.AccessRightSet;

import java.util.HashSet;
import java.util.Set;

public class And extends Pattern<Object> {

    public static And and(Pattern<Object>... args) {
        return new And(args);
    }

    private Pattern<Object>[] args;

    public And(Pattern<Object>[] args) {
        this.args = args;
    }

    @Override
    public boolean matches(Object value, GraphReview graphReview) throws PMException {
        // evaluate all args, if one is false return false
        for (Pattern<Object> arg : args) {
            if (!arg.matches(value, graphReview)) {
                return false;
            }
        }

        return true;
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
