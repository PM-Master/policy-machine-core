package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.pap.query.PolicyQuery;

public abstract class Modifier {

    protected final PolicyQuery querier;

    public Modifier(PolicyQuery policyQuery) {
        this.querier = policyQuery;
    }
}
