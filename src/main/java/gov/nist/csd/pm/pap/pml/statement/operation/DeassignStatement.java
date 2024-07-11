package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.graph.DeassignOp;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DeassignStatement extends OperationStatement {

    private Expression ascendant;
    private Expression deassignFrom;

    public DeassignStatement(Expression ascendant, Expression deassignFrom) {
        super(new DeassignOp());
        this.ascendant = ascendant;
        this.deassignFrom = deassignFrom;
    }

    @Override
    public List<Object> prepareOperands(ExecutionContext ctx, PAP pap)
            throws PMException {
        String asc = ctx.executeStatement(pap, ascendant).getStringValue();
        List<Value> deassignFromValue = ctx.executeStatement(pap, deassignFrom).getArrayValue();
        List<String> descs = new ArrayList<>();
        for (Value value : deassignFromValue) {
            descs.add(value.getStringValue());
        }

        return List.of(asc, descs);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("deassign %s from %s", ascendant, deassignFrom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeassignStatement that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(ascendant, that.ascendant) && Objects.equals(
                deassignFrom,
                that.deassignFrom
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ascendant, deassignFrom);
    }
}
