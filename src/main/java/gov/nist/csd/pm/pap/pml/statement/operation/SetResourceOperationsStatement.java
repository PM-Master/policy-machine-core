package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.operation.SetResourceOperationsOp;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SetResourceOperationsStatement extends OperationStatement {

    private Expression ops;

    public SetResourceOperationsStatement(Expression ops) {
        super(new SetResourceOperationsOp());

        this.ops = ops;
    }

    @Override
    public Map<String, Object> prepareOperands(ExecutionContext ctx, PAP pap)
            throws PMException {
        Value arValue = ctx.executeStatement(pap, ops);
        AccessRightSet accessRightSet = new AccessRightSet();
        for (Value v : arValue.getArrayValue()) {
            accessRightSet.add(v.getStringValue());
        }

        return List.of(accessRightSet);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("set resource access rights %s", ops);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SetResourceOperationsStatement that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(ops, that.ops);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ops);
    }
}
