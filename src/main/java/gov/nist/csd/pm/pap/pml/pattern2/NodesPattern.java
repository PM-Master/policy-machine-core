package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.pap.pml.function.PMLFunction;
import gov.nist.csd.pm.pap.pml.function.PMLPreparedOperation;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;
import java.util.Map;

public class NodesPattern extends PMLPatternFunction {

    public NodesPattern() {
        super("nodes", Type.bool(), Map.of("nodes", new PMLRequiredCapability(0, Type.array(Type.string()))), (pap, operands) -> {
            Value nodes = (Value) operands.get("nodes");
            Value value = (Value) operands.get("value");

            List<Value> arrayValue = nodes.getArrayValue();
            return new BoolValue(arrayValue.contains(value));
        });
    }
}
