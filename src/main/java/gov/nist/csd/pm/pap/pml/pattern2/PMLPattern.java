package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.pattern.Pattern;
import gov.nist.csd.pm.common.pattern.ReferencedNodes;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationRuntimeException;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.FunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.expression.LogicalExpression;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.Literal;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.scope.ExecuteGlobalScope;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PMLPattern extends Pattern {

    private PatternExpression expression;

    public PMLPattern(VisitorContext visitorContext, PMLParser.PatternContext ctx) {
        this.expression = new PatternExpression(Expression.compile(visitorContext, ctx.expression(), Type.bool()));

        checkPatternExpressions(ctx, expression);
    }

    public Expression getExpression() {
        return expression;
    }

    public List<Literal> getLiterals() {
        return getLiterals(expression);
    }

    private List<Literal> getLiterals(Expression expression) {
        List<Literal> list = new ArrayList<>();

        if (expression instanceof LogicalExpression logicalExpression) {
            list.addAll(getLiterals(logicalExpression.getLeft()));
            list.addAll(getLiterals(logicalExpression.getRight()));
        } else if (expression instanceof PatternFunctionInvokeExpression patternInvoke) {
            List<Expression> actualArgs = patternInvoke.getActualArgs();
            for (Expression actualArg : actualArgs) {
                list.addAll(getLiterals(actualArg));
            }
        } else if (expression instanceof Literal literal) {
            list.add(literal);
        }

        return list;
    }

    private void checkPatternExpressions(PMLParser.PatternContext ctx, Expression expression) {
        if (expression instanceof LogicalExpression logicalExpression) {
            checkPatternExpressions(ctx, logicalExpression.getLeft());
            checkPatternExpressions(ctx, logicalExpression.getRight());
        } else if (expression instanceof PatternFunctionInvokeExpression patternInvoke) {
            List<Expression> actualArgs = patternInvoke.getActualArgs();
            for (Expression actualArg : actualArgs) {
                checkPatternExpressions(ctx, actualArg);
            }
        } else if (expression instanceof Literal literal &&
                (literal instanceof StringLiteral ||
                        (literal instanceof ArrayLiteral arrayLiteral && arrayLiteral.getType().equals(Type.array(Type.string()))))) {
            return;
        }

        throw new PMLCompilationRuntimeException(ctx, "pattern expected function invoke, logical expression, or string/string[] literal");
    }

    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        ExecutionContext executionContext = new ExecutionContext(new UserContext(), new ExecuteGlobalScope());
        executionContext.scope().local().addVariable("value", Value.fromObject(value));
        return expression.execute(executionContext, pap)
                         .getBooleanValue();
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        List<Literal> literals = getLiterals();

        // if no literals than it's any
        if (literals.isEmpty()) {
            return new ReferencedNodes(true);
        }

        // get literals from all expressions
        Set<String> nodes = new HashSet<>();
        for (Literal literal : literals) {
            if (literal instanceof StringLiteral strLit) {
                nodes.add(strLit.getValue());
            } else if (literal instanceof ArrayLiteral arrayLit) {
                for (Expression e : arrayLit.getArray()) {
                    if (e instanceof StringLiteral eLit) {
                        nodes.add(eLit.getValue());
                    }
                }
            }
        }

        return new ReferencedNodes(nodes, false);
    }

    @Override
    public String toString() {
        return expression.toFormattedString(0);
    }
}
