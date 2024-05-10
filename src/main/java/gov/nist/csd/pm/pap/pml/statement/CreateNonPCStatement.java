package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;

import java.util.*;


public class CreateNonPCStatement extends PMLStatement{
    private Expression name;
    private NodeType type;
    private Expression assignTo;
    private Expression withProperties;

    public CreateNonPCStatement(Expression name, NodeType type, Expression assignTo) {
        this.name = name;
        this.type = type;
        this.assignTo = assignTo;
        this.withProperties = null;
    }

    public CreateNonPCStatement(Expression name, NodeType type, Expression assignTo, Expression withProperties) {
        this.name = name;
        this.type = type;
        this.assignTo = assignTo;
        this.withProperties = withProperties;
    }

    public Expression getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    public Expression getAssignTo() {
        return assignTo;
    }

    public Expression getWithProperties() {
        return withProperties;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        Value nameValue = name.execute(ctx, pap);
        Value assignToValue = assignTo.execute(ctx, pap);

        List<String> parents = new ArrayList<>();

        List<Value> arrayValue = assignToValue.getArrayValue();
        for (Value parentValue : arrayValue) {
            parents.add(parentValue.getStringValue());
        }

        switch (type) {
            case UA -> pap.modify().graph().createUserAttribute(
                    nameValue.getStringValue(),
                    new HashMap<>(),
                    parents
            );
            case OA -> pap.modify().graph().createObjectAttribute(
                    nameValue.getStringValue(),
                    new HashMap<>(),
                    parents
            );
            case U -> pap.modify().graph().createUser(
                    nameValue.getStringValue(),
                    new HashMap<>(),
                    parents
            );
            case O -> pap.modify().graph().createObject(
                    nameValue.getStringValue(),
                    new HashMap<>(),
                    parents
            );
        }

        if (withProperties != null) {
            Value propertiesValue = withProperties.execute(ctx, pap);

            Map<String, String> properties = new HashMap<>();
            for (Map.Entry<Value, Value> e : propertiesValue.getMapValue().entrySet()) {
                properties.put(e.getKey().getStringValue(), e.getValue().getStringValue());
            }

            pap.modify().graph().setNodeProperties(nameValue.getStringValue(), properties);
        }

        return new VoidValue();
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
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateNonPCStatement that = (CreateNonPCStatement) o;
        return Objects.equals(name, that.name) && type == that.type && Objects.equals(
                assignTo, that.assignTo) && Objects.equals(withProperties, that.withProperties);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, type, withProperties != null ? withProperties : "", assignTo);
    }
}
