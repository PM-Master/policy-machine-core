package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.pattern.Pattern;
import gov.nist.csd.pm.common.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.PAP;

public class AscendantOfPattern extends Pattern {

    private String descendant;

    public AscendantOfPattern(String descendant) {
        this.descendant = descendant;
    }

    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        return pap;
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        return null;
    }

    @Override
    public PatternExpression toPatternExpression() {
        return null;
    }
}
