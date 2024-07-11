package gov.nist.csd.pm.common.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;

public class AnyPattern extends Pattern {
    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        return true;
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        return new ReferencedNodes(true);
    }
}
