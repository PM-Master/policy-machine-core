package gov.nist.csd.pm.common.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;

import java.util.List;

public class AndPattern extends Pattern{

    protected List<Pattern> patterns;

    public AndPattern(List<Pattern> patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        for (Pattern pattern : patterns) {
            if (!pattern.matches(value, pap)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        ReferencedNodes referencedNodes = new ReferencedNodes(false);

        for (Pattern pattern : patterns) {
            referencedNodes = referencedNodes.combine(pattern.getReferencedNodes());
        }

        return referencedNodes;
    }
}
