package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.Objects;


public class DeleteStatement extends PMLStatement {

    private Type type;
    private Expression expression;

    public DeleteStatement(Type type, Expression expression) {
        this.type = type;
        this.expression = expression;
    }

    public Type getType() {
        return type;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        String name = expression.execute(ctx, policy).getStringValue();
        if (type == Type.PROHIBITION) {
            policy.modify().prohibitions().delete(name);
        } else if (type == Type.OBLIGATION) {
            policy.modify().obligations().delete(name);
        } else {
            policy.modify().graph().deleteNode(name);
        }

        return new VoidValue();
    }

    @Override
    public String toFormattedString(int indentLevel) {
        String typeStr = "";
        switch (type) {
            case PROHIBITION -> typeStr = "prohibition";
            case OBLIGATION -> typeStr = "obligation";
            case POLICY_CLASS -> typeStr = "PC";
            case OBJECT_ATTRIBUTE -> typeStr = "OA";
            case USER_ATTRIBUTE -> typeStr = "UA";
            case OBJECT -> typeStr = "O";
            case USER -> typeStr = "U";
        }

        return indent(indentLevel) + String.format("delete %s %s", typeStr, expression);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteStatement that = (DeleteStatement) o;
        return type == that.type && Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, expression);
    }

    public enum Type {
        POLICY_CLASS,
        OBJECT_ATTRIBUTE,
        USER_ATTRIBUTE,
        OBJECT,
        USER,
        PROHIBITION,
        OBLIGATION
    }
}
