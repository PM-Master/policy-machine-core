package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Objects;

public class ErrorExpression extends Expression {

    private final ParserRuleContext ctx;

    public ErrorExpression(ParserRuleContext ctx) {
        this.ctx = ctx;
    }
    @Override
    public Type getType(Scope scope) throws PMLScopeException {
        return Type.any();
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyModification policyModification) throws PMException {
        return new StringValue(this.ctx.getText());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ErrorExpression that = (ErrorExpression) o;
        return Objects.equals(ctx, that.ctx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ctx.getText());
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return ctx.getText();
    }

}
