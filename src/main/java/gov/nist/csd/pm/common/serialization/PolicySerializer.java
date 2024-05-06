package gov.nist.csd.pm.common.serialization;

import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;

public interface PolicySerializer {

    String serialize(PolicyModification policyModification) throws PMException;

}
