package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.obligation.UpdateObligationOp;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class DeleteRuleStatement extends OperationStatement {

    private Expression ruleExpr;
    private Expression oblExpr;

    public DeleteRuleStatement(Expression ruleExpr, Expression oblExpr) {
        super(new UpdateObligationOp());
        this.ruleExpr = ruleExpr;
        this.oblExpr = oblExpr;
    }

    @Override
    public Map<String, Object> prepareOperands(ExecutionContext ctx, PAP pap) throws PMException {
        String ruleName = ctx.executeStatement(pap, ruleExpr).getStringValue();
        String oblName = ctx.executeStatement(pap, oblExpr).getStringValue();

        Obligation obligation = pap.query().obligations().get(oblName);
        List<Rule> rules = new ArrayList<>();
        for (Rule rule : obligation.getRules()) {
            if (rule.getName().equals(ruleName)) {
                continue;
            }

            rules.add(rule);
        }

        return List.of(
                obligation.getAuthor(),
                obligation.getName(),
                rules
        );
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + String.format("delete rule %s from obligation %s", ruleExpr, oblExpr);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeleteRuleStatement that)) {
            return false;
        }
        return Objects.equals(ruleExpr, that.ruleExpr) && Objects.equals(oblExpr, that.oblExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleExpr, oblExpr);
    }
}
