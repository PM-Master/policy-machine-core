package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.List;
import java.util.Objects;


public class DissociateStatement extends PMLStatement {

    private Expression uaExpr;
    private Expression targetExpr;

    public DissociateStatement(Expression uaExpr, Expression targetExpr) {
        this.uaExpr = uaExpr;
        this.targetExpr = targetExpr;
    }

    public Expression getUaExpr() {
        return uaExpr;
    }

    public Expression getTargetExpr() {
        return targetExpr;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException {
        String ua = uaExpr.execute(ctx, policy).getStringValue();
        List<Value> targets = targetExpr.execute(ctx, policy).getArrayValue();

        for (Value target : targets) {
            policy.modify().graph().dissociate(ua, target.getStringValue());
        }

        return new VoidValue();
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("dissociate %s and %s", uaExpr, targetExpr);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DissociateStatement that = (DissociateStatement) o;
        return Objects.equals(uaExpr, that.uaExpr) && Objects.equals(targetExpr, that.targetExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uaExpr, targetExpr);
    }
}
