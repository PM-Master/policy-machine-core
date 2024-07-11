package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.graph.CreatePolicyClassOp;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreatePolicyStatement extends OperationStatement {

    private Expression name;
    private Expression properties;
    
    public CreatePolicyStatement(Expression name, Expression properties) {
        super(new CreatePolicyClassOp());
        
        this.name = name;
        this.properties = properties;
    }

    public CreatePolicyStatement(Expression name) {
        super(new CreatePolicyClassOp());
        
        this.name = name;
    }

    @Override
    public List<Object> prepareOperands(ExecutionContext ctx, PAP pap)
            throws PMException {
        String pcName = ctx.executeStatement(pap, name).getStringValue();

        Map<String, String> props = new HashMap<>();
        if (properties != null) {
            Value propertiesValue = ctx.executeStatement(pap, properties);
            for (Map.Entry<Value, Value> e : propertiesValue.getMapValue().entrySet()) {
                props.put(e.getKey().getStringValue(), e.getValue().getStringValue());
            }
        }

        return List.of(pcName, props);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        String propertiesStr = (properties == null ? "" : " with properties " + properties);
        return indent(indentLevel) + String.format("create PC %s%s", name, propertiesStr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreatePolicyStatement that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(name, that.name) && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, properties);
    }
}
