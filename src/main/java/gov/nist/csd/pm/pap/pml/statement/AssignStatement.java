package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;

import java.util.List;
import java.util.Objects;


public class AssignStatement extends PMLStatement {

    private Expression ascendant;
    private Expression assignTo;

    public AssignStatement(Expression ascendant, Expression assignTo) {
        this.ascendant = ascendant;
        this.assignTo = assignTo;
    }

    public Expression getAscendant() {
        return ascendant;
    }

    public Expression getAssignTo() {
        return assignTo;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        Value ascValue = this.ascendant.execute(ctx, policy);
        Value assignToValue = this.assignTo.execute(ctx, policy);

        String ascStringValue = ascValue.getStringValue();

        List<Value> valueArr = assignToValue.getArrayValue();
        for (Value value : valueArr) {
            String descendant = value.getStringValue();
            policy.modify().graph().assign(ascStringValue, descendant);
        }

        return new VoidValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignStatement that = (AssignStatement) o;
        return Objects.equals(ascendant, that.ascendant) && Objects.equals(assignTo, that.assignTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ascendant, assignTo);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("assign %s to %s", ascendant, assignTo);
    }
}
