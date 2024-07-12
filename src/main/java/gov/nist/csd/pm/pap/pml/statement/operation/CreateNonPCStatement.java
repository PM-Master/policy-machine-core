package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.graph.CreateObjectAttributeOp;
import gov.nist.csd.pm.pap.op.graph.CreateObjectOp;
import gov.nist.csd.pm.pap.op.graph.CreateUserAttributeOp;
import gov.nist.csd.pm.pap.op.graph.CreateUserOp;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.*;

import static gov.nist.csd.pm.pap.op.graph.CreateNodeOp.*;


public class CreateNonPCStatement extends OperationStatement {
    private Expression name;
    private NodeType type;
    private Expression assignTo;
    private Expression withProperties;

    public CreateNonPCStatement(Expression name, NodeType type, Expression assignTo) {
        super(getOpFromType(type));
        this.name = name;
        this.type = type;
        this.assignTo = assignTo;
        this.withProperties = null;
    }

    public CreateNonPCStatement(Expression name, NodeType type, Expression assignTo, Expression withProperties) {
        super(getOpFromType(type));
        this.name = name;
        this.type = type;
        this.assignTo = assignTo;
        this.withProperties = withProperties;
    }

    @Override
    public Map<String, Object> prepareOperands(ExecutionContext ctx, PAP pap) throws PMException {
        Value nameValue = ctx.executeStatement(pap, name);
        Value assignToValue = ctx.executeStatement(pap, assignTo);

        List<String> descendants = new ArrayList<>();
        List<Value> arrayValue = assignToValue.getArrayValue();
        for (Value descValue : arrayValue) {
            descendants.add(descValue.getStringValue());
        }

        Map<String, String> properties = new HashMap<>();
        if (withProperties != null) {
            Value propertiesValue = ctx.executeStatement(pap, withProperties);

            for (Map.Entry<Value, Value> e : propertiesValue.getMapValue().entrySet()) {
                properties.put(e.getKey().getStringValue(), e.getValue().getStringValue());
            }
        }

        return Map.of(
                NAME_OPERAND, nameValue.getStringValue(),
                PROPERTIES_OPERAND, properties,
                DESCENDANTS_OPERAND, descendants
        );
    }
    
    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format(
                "create %s %s %sassign to %s",
                type.toString(),
                name,
                withProperties != null ? "with properties " + withProperties + " ": "",
                assignTo
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateNonPCStatement that)) {
            return false;
        }
        return Objects.equals(name, that.name) && type == that.type && Objects.equals(
                assignTo,
                that.assignTo
        ) && Objects.equals(withProperties, that.withProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, assignTo, withProperties);
    }

    private static Operation<Void> getOpFromType(NodeType type) {
        return switch (type) {
            case OA -> new CreateObjectAttributeOp();
            case O -> new CreateObjectOp();
            case UA -> new CreateUserAttributeOp();
            default -> new CreateUserOp();
        };
    }
}
