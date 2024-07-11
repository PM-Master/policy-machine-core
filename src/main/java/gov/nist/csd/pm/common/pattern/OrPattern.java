package gov.nist.csd.pm.common.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;

import java.util.List;

public class OrPattern extends AndPattern {

    public OrPattern(List<Pattern> patterns) {
        super(patterns);
    }

    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        for (Pattern pattern : patterns) {
            if (pattern.matches(value, pap)) {
                return true;
            }
        }

        return true;
    }
}
