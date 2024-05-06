package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.op.pattern.ReferencedPolicyEntities;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.PolicyQuery;

import java.util.Objects;

public class PMLPattern<T> extends Pattern<T> {

    private String varName;
    private Expression patternExpr;

    public PMLPattern(String varName, Expression patternExpr) {
        this.varName = varName;
        this.patternExpr = patternExpr;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public Expression getPatternExpr() {
        return patternExpr;
    }

    public void setPatternExpr(Expression patternExpr) {
        this.patternExpr = patternExpr;
    }

    public Statement getStatement() {
        return new Statement(this);
    }

    @Override
    public boolean matches(T value, PolicyQuery querier) throws PMException {
        // TODO need PAP not GraphReview
        return true;
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        return null;
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
        return Objects.equals(varName, that.varName) && Objects.equals(patternExpr, that.patternExpr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(varName, patternExpr);
    }

    public static class Statement extends PMLStatement {

        private PMLPattern pattern;

        public Statement(PMLPattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Value execute(ExecutionContext ctx, PolicyModification policyModification) throws PMException {
            return pattern.patternExpr.execute(ctx, policyModification);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Statement statement = (Statement) o;
            return Objects.equals(pattern, statement.pattern);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(pattern);
        }

        @Override
        public String toFormattedString(int indentLevel) {
            return indent(indentLevel) + pattern.varName + " => " + pattern.patternExpr.toFormattedString(0);
        }
    }
}
