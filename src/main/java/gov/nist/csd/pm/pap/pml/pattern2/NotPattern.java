package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.pattern.Pattern;
import gov.nist.csd.pm.common.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.PAP;

public class NotPattern extends Pattern {
    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        return null;
    }
}
