package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.type.Type;

public class PMLPatternArg extends FormalArgument {
    public PMLPatternArg(String name, Type type) {
        super(name, type);

        // if type is not string or []string or pattern or []pattern
        if (!(type.isString() ||
                type.isPattern() ||
                (type.isArray() && (type.getArrayElementType().isString() || type.getArrayElementType().isPattern())))) {
            throw new IllegalArgumentException("PMLPatterns only accept string, []string, and other patterns as args, received " + type);
        }
    }
}
