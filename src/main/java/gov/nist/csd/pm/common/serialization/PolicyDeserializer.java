package gov.nist.csd.pm.common.serialization;

import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;

public interface PolicyDeserializer {

    void deserialize(Policy policy, UserContext author, String input) throws PMException;

}
