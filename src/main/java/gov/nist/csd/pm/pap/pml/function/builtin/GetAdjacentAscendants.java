package gov.nist.csd.pm.pap.pml.function.builtin;

import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.function.PMLPolicyFunction;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GetAdjacentAscendants extends PMLPolicyFunction {

    private static final Type returnType = Type.array(Type.string());

    public GetAdjacentAscendants() {
        super(
                "getAdjacentAscendants",
                Type.array(Type.any()),
                Map.of(
                        "nodeName", new PMLRequiredCapability(0, Type.string())
                ),
                (pap, operands) -> {
                    Value nodeName = (Value) operands.get("nodeName");

                    Collection<String> ascendants = pap.query().graph().getAdjacentAscendants(nodeName.getStringValue());
                    List<Value> ascValues = new ArrayList<>(ascendants.size());

                    ascendants.forEach(ascendant -> ascValues.add(new StringValue(ascendant)));

                    return new ArrayValue(ascValues, returnType);
                }
        );
    }
}
