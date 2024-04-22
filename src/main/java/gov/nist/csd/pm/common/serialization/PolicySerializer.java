package gov.nist.csd.pm.common.serialization;

import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;

public interface PolicySerializer {

    String serialize(Policy policy) throws PMException;

}
