package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SetNodePropertiesStatement extends PMLStatement {

    private Expression nameExpr;
    private Expression propertiesExpr;

    public SetNodePropertiesStatement(Expression nameExpr, Expression propertiesExpr) {
        this.nameExpr = nameExpr;
        this.propertiesExpr = propertiesExpr;
    }

    public Expression getNameExpr() {
        return nameExpr;
    }

    public Expression getPropertiesExpr() {
        return propertiesExpr;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        String name = nameExpr.execute(ctx, policy).getStringValue();
        Map<Value, Value> map = propertiesExpr.execute(ctx, policy).getMapValue();
        Map<String, String> properties = new HashMap<>();
        for (Value key : map.keySet()) {
            properties.put(key.getStringValue(), map.get(key).getStringValue());
        }

        policy.modify().graph().setNodeProperties(name, properties);

        return new VoidValue();
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("set properties of %s to %s", nameExpr, propertiesExpr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetNodePropertiesStatement that = (SetNodePropertiesStatement) o;
        return Objects.equals(nameExpr, that.nameExpr)
                && Objects.equals(propertiesExpr, that.propertiesExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameExpr, propertiesExpr);
    }
}
