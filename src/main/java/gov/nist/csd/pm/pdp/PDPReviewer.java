package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.PolicyQuery;
import gov.nist.csd.pm.pdp.adjudicator.*;

public class PDPReviewer implements PolicyQuery {

    private final PDPAccessQuery accessReviewer;
    private final PDPGraphQuery graphReviewer;
    private final PDPProhibitionsQuery prohibitionsReviewer;
    private final PDPObligationsQuery obligationsReviewer;

    public PDPReviewer(UserContext userCtx, PAP pap, PrivilegeChecker privilegeChecker) {
        PolicyQuery review = pap.review();
        this.accessReviewer = new PDPAccessQuery(new AdjudicatorAccessQuery(userCtx, privilegeChecker), review.access());
        this.graphReviewer = new PDPGraphQuery(new AdjudicatorGraphQuery(userCtx, privilegeChecker), review.graph());
        this.prohibitionsReviewer = new PDPProhibitionsQuery(new AdjudicatorProhibitionsQuery(userCtx, privilegeChecker), review.prohibitions());
        this.obligationsReviewer = new PDPObligationsQuery(new AdjudicatorObligationsQuery(userCtx, privilegeChecker), review.obligations());
    }

    @Override
    public PDPAccessQuery access() {
        return accessReviewer;
    }

    @Override
    public PDPGraphQuery graph() {
        return graphReviewer;
    }

    @Override
    public PDPProhibitionsQuery prohibitions() {
        return prohibitionsReviewer;
    }

    @Override
    public PDPObligationsQuery obligations() {
        return obligationsReviewer;
    }
}
