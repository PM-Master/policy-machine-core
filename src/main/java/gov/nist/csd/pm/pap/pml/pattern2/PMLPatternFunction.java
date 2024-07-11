package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;

import java.util.List;

public abstract class PMLPatternFunction extends PMLFunction<BoolValue> {

    public PMLPatternFunction(String opName, List<PMLRequiredCapability> capMap) {
        super(opName, Type.bool(), capMap);
    }
}
