package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.PatternValue;
import gov.nist.csd.pm.pap.pml.value.Value;

public class PatternExpression extends Expression{



    @Override
    public Type getType(Scope<Variable> scope) throws PMLScopeException {
        return Type.pattern();
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        return new PatternValue();
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return "";
    }
}
