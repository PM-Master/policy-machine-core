package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.graph.SetNodePropertiesOp;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SetNodePropertiesStatement extends OperationStatement {

    private Expression nameExpr;
    private Expression propertiesExpr;

    public SetNodePropertiesStatement(Expression nameExpr, Expression propertiesExpr) {
        super(new SetNodePropertiesOp());
        this.nameExpr = nameExpr;
        this.propertiesExpr = propertiesExpr;
    }

    @Override
    public List<Object> prepareOperands(ExecutionContext ctx, PAP pap) throws PMException {
        String name = ctx.executeStatement(pap, nameExpr).getStringValue();
        Map<Value, Value> map = ctx.executeStatement(pap, propertiesExpr).getMapValue();
        Map<String, String> properties = new HashMap<>();
        for (Value key : map.keySet()) {
            properties.put(key.getStringValue(), map.get(key).getStringValue());
        }
        
        return List.of(name, properties);
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
