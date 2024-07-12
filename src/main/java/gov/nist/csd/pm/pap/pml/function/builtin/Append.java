package gov.nist.csd.pm.pap.pml.function.builtin;

import gov.nist.csd.pm.pap.pml.function.PMLFunction;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;
import java.util.Map;

public class Append extends PMLFunction {
    public Append() {
        super(
                "append",
                Type.array(Type.any()),
                Map.of(
                        "dst", new PMLRequiredCapability(0, Type.string()),
                        "src", new PMLRequiredCapability(1, Type.string())
                ),
                (pap, operands) -> {
                    List<Value> valueArr = (List<Value>) operands.get("dst");
                    Value srcValue = (Value) operands.get("src");

                    valueArr.add(srcValue);

                    return new ArrayValue(valueArr, Type.array(Type.any()));
                }
        );
    }
}
