package gov.nist.csd.pm.pap.pml.expression.reference;

import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

public class ReferenceByBracketIndex extends ReferenceByIndex{
    public ReferenceByBracketIndex(VariableReference varRef, Expression index) {
        super(varRef, index);
    }

    @Override
    public Value execute(ExecutionContext ctx, Policy policy) throws PMException {
        Value value = varRef.execute(ctx, policy);
        if (!value.getType().isMap()) {
            return value;
        }

        Map<Value, Value> mapValue = value.getMapValue();
        Value indexValue = index.execute(ctx, policy);

        return mapValue.get(indexValue);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return varRef + "[" + index + "]";
    }
}
