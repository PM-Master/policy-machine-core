package gov.nist.csd.pm.common.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;

import java.util.HashSet;
import java.util.List;

public class AscendantOfPattern extends Pattern {

    protected String node;

    public AscendantOfPattern(String node) {
        this.node = node;
    }


    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        if (!(value instanceof String strValue)) {
            return false;
        }

        return pap.query().graph().isAscendant(strValue, node);
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        return new ReferencedNodes(new HashSet<>(List.of(node)), false);
    }
}
