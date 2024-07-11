package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class Concat extends PMLFunction {

    private static final String ARR_ARG = "arr";

    public Concat() {
        super("concat", Type.string(), List.of(new PMLRequiredCapability(ARR_ARG, Type.array(Type.string()))));
    }

    @Override
    public Value execute(PAP pap) throws PMException {
        List<Value> arr = getCtx().scope().getVariable(ARR_ARG).getArrayValue();
        StringBuilder s = new StringBuilder();
        for (Value v : arr) {
            s.append(v.getStringValue());
        }

        return new StringValue(s.toString());
    }
}
