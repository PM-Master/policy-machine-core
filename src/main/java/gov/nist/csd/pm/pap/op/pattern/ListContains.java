package gov.nist.csd.pm.pap.op.pattern;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.query.PolicyQuery;

import java.util.List;
import java.util.Set;

public class ListContains extends Pattern<List> {

    public static ListContains listContains(Object arg) {
        return new ListContains(arg);
    }

    private Object arg;

    public ListContains(Object arg) {
        this.arg = arg;
    }

    @Override
    public boolean matches(List value, PolicyQuery querier) {
        return value.contains(arg);
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        return new ReferencedPolicyEntities(Set.of(String.valueOf(arg)), new AccessRightSet(), false);
    }

}