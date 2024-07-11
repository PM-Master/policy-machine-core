package gov.nist.csd.pm.common.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;

public class DescendantOfPattern extends AscendantOfPattern {
    public DescendantOfPattern(String node) {
        super(node);
    }

    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        if (!(value instanceof String strValue)) {
            return false;
        }

        return pap.query().graph().getAttributeDescendants(strValue).contains(node) ||
                pap.query().graph().getPolicyClassDescendants(strValue).contains(node);
    }
}
