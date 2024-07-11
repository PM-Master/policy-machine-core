package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.*;

public class PolicyQueryAdjudicator implements PolicyQuery {

    private final AccessQueryAdjudicator access;
    private final GraphQueryAdjudicator graph;
    private final ProhibitionsQueryAdjudicator prohibitions;
    private final ObligationsQueryAdjudicator obligations;

    public PolicyQueryAdjudicator(UserContext userCtx, PAP pap) {
        this.access = new AccessQueryAdjudicator(userCtx, pap);
        this.graph = new GraphQueryAdjudicator(userCtx, pap);
        this.prohibitions = new ProhibitionsQueryAdjudicator(userCtx, pap);
        this.obligations = new ObligationsQueryAdjudicator(userCtx, pap);
    }

    @Override
    public AccessQuery access() {
        return access;
    }

    @Override
    public GraphQuery graph() {
        return graph;
    }

    @Override
    public ProhibitionsQuery prohibitions() {
        return prohibitions;
    }

    @Override
    public ObligationsQuery obligations() {
        return obligations;
    }

    @Override
    public OperationsQuery operations() {
        return null;
    }

}
