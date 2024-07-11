package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.List;

public class PMLPatternReqCap extends PMLRequiredCapability {

    public PMLPatternReqCap(String operand, Type type) {
        super(operand, type);
        checkType();
    }

    private void checkType() {
        // if type is not string or []string or pattern or []pattern
        if (!(type.isString() ||
                type.isPattern() ||
                (type.isArray() && (type.getArrayElementType().isString() || type.getArrayElementType().isPattern())))) {
            throw new IllegalArgumentException("PMLPatterns only accept string, []string, and other patterns as args, received " + type);
        }
    }
}
