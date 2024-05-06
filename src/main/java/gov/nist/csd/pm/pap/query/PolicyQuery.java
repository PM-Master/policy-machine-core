package gov.nist.csd.pm.pap.query;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.serialization.PolicySerializer;

public interface PolicyQuery {

    AccessQuery access();
    GraphQuery graph();
    ProhibitionsQuery prohibitions();
    ObligationsQuery obligations();
    PMLQuery pml();

    String serialize(PolicySerializer serializer) throws PMException;
}
