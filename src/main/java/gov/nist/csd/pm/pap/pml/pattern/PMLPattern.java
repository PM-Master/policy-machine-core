package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.obligation.pattern.Pattern;
import gov.nist.csd.pm.common.obligation.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.PatternValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class PMLPattern extends Pattern {

    protected UserContext author;
    protected Expression expression;

    public PMLPattern(UserContext author, Expression expression) {
        this.author = author;
        this.expression = expression;
    }

    @Override
    public abstract boolean matches(Object value, PAP pap) throws PMException;

    @Override
    public abstract ReferencedNodes getReferencedNodes();

    public Expression getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PMLPattern that = (PMLPattern) o;
        return Objects.equals(expression, that.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expression);
    }

    @Override
    public String toString() {
        return "PMLPattern{" +
                "expression=" + expression +
                '}';
    }
}
