package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.*;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pdp.PDPEventEmitter;

public class PolicyModificationAdjudicator implements PolicyModification {

    private final GraphModificationAdjudicator graph;
    private final ProhibitionsModificationAdjudicator prohibitions;
    private final ObligationsModificationAdjudicator obligations;

    public PolicyModificationAdjudicator(UserContext userCtx, PAP pap, EventEmitter eventEmitter) {
        this.graph = new GraphModificationAdjudicator(userCtx, pap, eventEmitter);
        this.prohibitions = new ProhibitionsModificationAdjudicator(userCtx, pap, eventEmitter);
        this.obligations = new ObligationsModificationAdjudicator(userCtx, pap, eventEmitter);
    }

    @Override
    public GraphModification graph() {
        return graph;
    }

    @Override
    public ProhibitionsModification prohibitions() {
        return prohibitions;
    }

    @Override
    public ObligationsModification obligations() {
        return obligations;
    }

    @Override
    public OperationsModification operations() {
        return null;
    }

    @Override
    public RoutinesModification routines() {
        return null;
    }

}
