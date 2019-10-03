package gov.nist.csd.pm.pdp.services;

import gov.nist.csd.pm.epp.EPP;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.decider.Decider;
import gov.nist.csd.pm.pdp.decider.PReviewDecider;
import gov.nist.csd.pm.pip.graph.Graph;
import gov.nist.csd.pm.pip.obligations.Obligations;
import gov.nist.csd.pm.pip.prohibitions.Prohibitions;

import java.util.Arrays;
import java.util.Set;

import static gov.nist.csd.pm.pdp.decider.PReviewDecider.ANY_OPERATIONS;

/**
 * Class to provide common methods to all services.
 */
public class Service {

    private PAP pap;
    private EPP epp;

    /**
     * Create a new Service with a sessionID and processID from the request context.
     * @param pap the Policy Administration Point
     * @param epp the Event Processing Point
     */
    Service(PAP pap, EPP epp) {
        this.pap = pap;
        this.epp = epp;
    }

    private Service() {}

    EPP getEPP() {
        return this.epp;
    }

    PAP getPAP() {
        return this.pap;
    }

    Graph getGraphPAP() {
        return pap.getGraphPAP();
    }

    Prohibitions getProhibitionsPAP() {
        return pap.getProhibitionsPAP();
    }

    Obligations getObligationsPAP() {
        return pap.getObligationsPAP();
    }

    public Decider getDecider() throws PMException {
        return new PReviewDecider(getGraphPAP());
    }

    boolean hasPermissions(UserContext userCtx, long targetID, String... permissions) throws PMException {
        Decider decider = new PReviewDecider(pap.getGraphPAP(), pap.getProhibitionsPAP());
        Set<String> perms = decider.list(userCtx.getUserID(), userCtx.getProcessID(), targetID);

        for(String p : permissions) {
            if(p.equals(ANY_OPERATIONS)) {
                return !perms.isEmpty();
            }
        }

        if (perms.contains("*")) {
            return true;
        } else {
            return !perms.isEmpty() && perms.containsAll(Arrays.asList(permissions));
        }
    }
}
