package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class UsersPattern extends PMLPatternFunction {

    public UsersPattern() {
        super("users", List.of(new PMLRequiredCapability("users", Type.array(Type.string()))));
    }

    @Override
    public BoolValue execute(PAP pap) throws PMException {
        Value patternValue = getCtx().scope().local().getVariable("patternValue");

        for (Object o : operands) {
            if (o.equals(patternValue)) {
                return new BoolValue(true);
            }
        }

        return new BoolValue(false);
    }
}
