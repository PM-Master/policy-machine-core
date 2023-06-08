package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.List;
import java.util.Objects;


public class AssignStatement extends PMLStatement {

    private Expression child;
    private Expression assignTo;

    public AssignStatement(Expression child, Expression assignTo) {
        this.child = child;
        this.assignTo = assignTo;
    }

    public Expression getChild() {
        return child;
    }

    public Expression getAssignTo() {
        return assignTo;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        Value childValue = this.child.execute(ctx, pap);
        Value assignToValue = this.assignTo.execute(ctx, pap);

        String childStringValue = childValue.getStringValue();

        List<Value> valueArr = assignToValue.getArrayValue();
        for (Value value : valueArr) {
            String parent = value.getStringValue();
            pap.modify().graph().assign(childStringValue, parent);
        }

        return new VoidValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignStatement that = (AssignStatement) o;
        return Objects.equals(child, that.child) && Objects.equals(assignTo, that.assignTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(child, assignTo);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("assign %s to %s", child, assignTo);
    }
}
