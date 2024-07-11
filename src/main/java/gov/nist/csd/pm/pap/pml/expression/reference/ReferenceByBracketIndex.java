package gov.nist.csd.pm.pap.pml.expression.reference;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

public class ReferenceByBracketIndex extends ReferenceByIndex{
    public ReferenceByBracketIndex(VariableReference varRef, Expression index) {
        super(varRef, index);
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        Value value = varRef.execute(ctx, pap);
        if (!value.getType().isMap()) {
            return value;
        }

        Map<Value, Value> mapValue = value.getMapValue();
        Value indexValue = index.execute(ctx, pap);

        return mapValue.get(indexValue);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return varRef + "[" + index + "]";
    }
}
