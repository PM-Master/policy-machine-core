package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.query.UserContext;

public interface AccessAdjudication {

    ResourceAdjudicationResponse adjudicateResourceAccess(UserContext user, Operation... operations) throws PMException;
    AdminAdjudicationResponse adjudicateAdminAccess(UserContext user, Operation ... operations) throws PMException;

}
