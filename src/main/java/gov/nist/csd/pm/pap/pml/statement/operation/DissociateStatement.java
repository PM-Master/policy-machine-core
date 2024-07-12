package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.graph.DissociateOp;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class DissociateStatement extends OperationStatement {

    private Expression uaExpr;
    private Expression targetExpr;

    public DissociateStatement(Expression uaExpr, Expression targetExpr) {
        super(new DissociateOp());
        this.uaExpr = uaExpr;
        this.targetExpr = targetExpr;
    }

    @Override
    public Map<String, Object> prepareOperands(ExecutionContext ctx, PAP pap) throws PMException {
        String ua = ctx.executeStatement(pap, uaExpr).getStringValue();
        String target = ctx.executeStatement(pap, targetExpr).getStringValue();

        return List.of(ua, target);
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
