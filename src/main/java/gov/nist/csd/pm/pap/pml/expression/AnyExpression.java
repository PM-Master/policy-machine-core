package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Objects;

public class AnyExpression extends Expression {

    private Expression e;

    public AnyExpression(Expression e) {
        this.e = e;
    }

    @Override
    public Type getType(Scope<Variable> scope) throws PMLScopeException {
        return e.getType(scope);
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        return e.execute(ctx, pap);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnyExpression that = (AnyExpression) o;
        return Objects.equals(e, that.e);
    }

    @Override
    public int hashCode() {
        return Objects.hash(e);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return e.toFormattedString(indentLevel);
    }
}
