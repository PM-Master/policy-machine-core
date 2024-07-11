package gov.nist.csd.pm.pap.pml.expression.reference;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

public class ReferenceByDotIndex extends ReferenceByIndex{

    private final String key;

    public ReferenceByDotIndex(VariableReference varRef, String index) {
        super(varRef, new ReferenceByID(index));
        this.key = index;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        Value value = varRef.execute(ctx, pap);

        if (!value.getType().isMap()) {
            return value;
        }

        Map<Value, Value> mapValue = value.getMapValue();

        return mapValue.get(new StringValue(key));
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return varRef + "." + index;
    }
}
