package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyReview;
import gov.nist.csd.pm.pdp.adjudicator.*;

public class PDPReviewer implements PolicyReview {

    private final PDPAccessReview accessReviewer;
    private final PDPGraphReview graphReviewer;
    private final PDPProhibitionsReview prohibitionsReviewer;
    private final PDPObligationsReview obligationsReviewer;

    public PDPReviewer(UserContext userCtx, PAP pap, PrivilegeChecker privilegeChecker) {
        PolicyReview review = pap.review();
        this.accessReviewer = new PDPAccessReview(new AdjudicatorAccessReview(userCtx, privilegeChecker), review.access());
        this.graphReviewer = new PDPGraphReview(new AdjudicatorGraphReview(userCtx, privilegeChecker), review.graph());
        this.prohibitionsReviewer = new PDPProhibitionsReview(new AdjudicatorProhibitionsReview(userCtx, privilegeChecker), review.prohibitions());
        this.obligationsReviewer = new PDPObligationsReview(new AdjudicatorObligationsReview(userCtx, privilegeChecker), review.obligations());
    }

    @Override
    public PDPAccessReview access() {
        return accessReviewer;
    }

    @Override
    public PDPGraphReview graph() {
        return graphReviewer;
    }

    @Override
    public PDPProhibitionsReview prohibitions() {
        return prohibitionsReviewer;
    }

    @Override
    public PDPObligationsReview obligations() {
        return obligationsReviewer;
    }
}
