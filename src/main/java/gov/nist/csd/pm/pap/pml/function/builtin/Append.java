package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.executable.PMLExecutable;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class Append extends PMLExecutable {
    public Append() {
        super(
                "append",
                Type.array(Type.any()),
                List.of(
                        new PMLRequiredCapability("dst", Type.string()),
                        new PMLRequiredCapability("src", Type.string())
                ),
                (pap, operands) -> {
                    List<Value> valueArr = (List<Value>) operands.get(0);
                    Value srcValue = (Value) operands.get(1);

                    valueArr.add(srcValue);

                    return new ArrayValue(valueArr, Type.array(Type.any()));
                }
        );
    }
}
