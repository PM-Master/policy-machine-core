package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.type.Type;

import java.util.List;
import java.util.stream.Collectors;

public class PatternFunctionSignature extends FunctionSignature {
    public PatternFunctionSignature(String functionName,
                                    List<PMLPatternArg> args) {
        super(functionName, Type.pattern(), args.stream().collect(Collectors.toUnmodifiableList()));
    }
}
