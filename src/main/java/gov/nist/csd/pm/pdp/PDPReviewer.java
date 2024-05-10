package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.serialization.PolicySerializer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.PMLQuery;
import gov.nist.csd.pm.pap.query.PolicyQuery;
import gov.nist.csd.pm.pdp.adjudicator.*;

public class PDPQuerier implements PolicyQuery {

    private final PDPAccessQuery accessQuerier;
    private final PDPGraphQuery graphQuerier;
    private final PDPProhibitionsQuery prohibitionsQuerier;
    private final PDPObligationsQuery obligationsQuerier;
    private final PDPPMLQuery pmlQuerier;

    public PDPQuerier(UserContext userCtx, PAP pap, PrivilegeChecker privilegeChecker) {
        PolicyQuery policyQuery = pap.query();
        this.accessQuerier = new PDPAccessQuery(new AdjudicatorAccessQuery(userCtx, privilegeChecker), policyQuery.access());
        this.graphQuerier = new PDPGraphQuery(new AdjudicatorGraphQuery(userCtx, privilegeChecker), policyQuery.graph());
        this.prohibitionsQuerier = new PDPProhibitionsQuery(new AdjudicatorProhibitionsQuery(userCtx, privilegeChecker), policyQuery.prohibitions());
        this.obligationsQuerier = new PDPObligationsQuery(new AdjudicatorObligationsQuery(userCtx, privilegeChecker), policyQuery.obligations());
        this.pmlQuerier = new PDPPMLQuery();
    }

    @Override
    public PDPAccessQuery access() {
        return accessQuerier;
    }

    @Override
    public PDPGraphQuery graph() {
        return graphQuerier;
    }

    @Override
    public PDPProhibitionsQuery prohibitions() {
        return prohibitionsQuerier;
    }

    @Override
    public PDPObligationsQuery obligations() {
        return obligationsQuerier;
    }

    @Override
    public PMLQuery pml() {
    }

    @Override
    public String serialize(PolicySerializer serializer) throws PMException {
    }
}
