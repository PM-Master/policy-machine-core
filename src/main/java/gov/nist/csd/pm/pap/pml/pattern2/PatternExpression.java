package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

public class PatternExpression extends Expression {

    private Expression expression;

    public PatternExpression(Expression expr) {
        expression = expr;
    }

    @Override
    public Type getType(Scope<Variable> scope) throws PMLScopeException {
        return null;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        return null;
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return "";
    }
}
