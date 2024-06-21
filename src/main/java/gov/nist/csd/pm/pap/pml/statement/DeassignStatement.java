package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.List;
import java.util.Objects;


public class DeassignStatement extends PMLStatement {

    private Expression ascendant;
    private Expression deassignFrom;

    public DeassignStatement(Expression ascendant, Expression deassignFrom) {
        this.ascendant = ascendant;
        this.deassignFrom = deassignFrom;
    }

    public Expression getAscendant() {
        return ascendant;
    }

    public Expression getDeassignFrom() {
        return deassignFrom;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        Value ascValue = ascendant.execute(ctx, policy);
        Value deassignFromValue = deassignFrom.execute(ctx, policy);

        String ascStringValue = ascValue.getStringValue();

        List<Value> valueArr = deassignFromValue.getArrayValue();
        for (Value value : valueArr) {
            String descendant = value.getStringValue();
            policy.modify().graph().deassign(ascStringValue, descendant);
        }

        return new VoidValue();
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("deassign %s from %s", ascendant, deassignFrom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeassignStatement that = (DeassignStatement) o;
        return Objects.equals(ascendant, that.ascendant) && Objects.equals(deassignFrom, that.deassignFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ascendant, deassignFrom);
    }
}
