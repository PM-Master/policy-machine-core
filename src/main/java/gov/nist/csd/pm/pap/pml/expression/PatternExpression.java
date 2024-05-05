package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;
import org.antlr.v4.runtime.ParserRuleContext;

public class PatternExpression extends Expression{

    private Expression expression;

    public PatternExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Type getType(Scope<Variable, FunctionSignature> scope) throws PMLScopeException {
        return Type.bool();
    }

    @Override
    public Value execute(ExecutionContext ctx, Policy policy) throws PMException {
        return expression.execute(ctx, policy);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return "";
    }
}
