package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;

public interface PMLExecutable {

    void executePML(UserContext userContext, String input)
            throws PMException;
}
