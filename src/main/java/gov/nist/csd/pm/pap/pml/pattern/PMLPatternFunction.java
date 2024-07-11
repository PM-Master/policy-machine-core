package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.function.PMLFunction;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.PatternValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public abstract class PMLPatternFunction extends PMLFunction<PatternValue> {

    public PMLPatternFunction(String name, List<PMLPatternReqCap> capMap) {
        super(name, Type.pattern(), new ArrayList<>(capMap));
    }

    public abstract PMLPattern getPattern(String varName, List<Value> argValues) throws PMException;

    @Override
    public abstract PatternValue execute(PAP pap) throws PMException;
}
