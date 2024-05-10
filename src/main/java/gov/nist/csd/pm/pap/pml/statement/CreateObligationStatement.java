package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.pattern.PatternExpression;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CreateObligationStatement extends PMLStatement {

    private Expression name;
    private List<CreateRuleStatement> ruleStmts;

    public CreateObligationStatement(Expression name, List<CreateRuleStatement> ruleStmts) {
        this.name = name;
        this.ruleStmts = ruleStmts;
    }

    public Expression getName() {
        return name;
    }

    public List<CreateRuleStatement> getRuleStmts() {
        return ruleStmts;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        UserContext author = ctx.author();
        String nameStr = name.execute(ctx, pap).getStringValue();

        // execute the create rule statements and add to obligation
        List<Rule> rules = new ArrayList<>();
        for (CreateRuleStatement createRuleStmt : ruleStmts) {
            Rule rule = createRuleStmt.execute(ctx, pap).getRuleValue();
            rules.add(rule);
        }

        pap.modify().obligations().create(author, nameStr, rules.toArray(rules.toArray(Rule[]::new)));

        return new VoidValue();
    }

    @Override
    public String toFormattedString(int indentLevel) {
        StringBuilder sb = new StringBuilder();
        for (CreateRuleStatement createRuleStatement : ruleStmts) {
            sb.append(createRuleStatement.toFormattedString(indentLevel+1)).append("\n");
        }

        String indent = indent(indentLevel);
        return String.format(
                """
                %screate obligation %s {
                %s%s}""", indent, name, sb, indent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateObligationStatement that = (CreateObligationStatement) o;
        return Objects.equals(name, that.name) && Objects.equals(ruleStmts, that.ruleStmts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ruleStmts);
    }

    public static CreateObligationStatement fromObligation(Obligation obligation) {
        return new CreateObligationStatement(
                new StringLiteral(obligation.getName()),
                createRuleStatementsFromObligation(obligation.getRules())
        );
    }

    private static List<CreateRuleStatement> createRuleStatementsFromObligation(List<Rule> rules) {
        List<CreateRuleStatement> createRuleStatements = new ArrayList<>();

        for (Rule rule : rules) {
            EventPattern event = rule.getEventPattern();

            List<PatternExpression> operandExprs = new ArrayList<>();
            int i = 0;
            for (Pattern operandPattern : event.getOperandPatterns()) {
                operandExprs.add(operandPattern.toPatternExpression());
                i++;
            }

            CreateRuleStatement createRuleStatement = new CreateRuleStatement(
                    new StringLiteral(rule.getName()),
                    event.getSubjectPattern().toPatternExpression(),
                    event.getOperationPattern().toPatternExpression(),
                    operandExprs,
                    new CreateRuleStatement.ResponseBlock(
                            rule.getResponse().getEventCtxVariable(),
                            rule.getResponse().getStatements()
                    )
            );

            createRuleStatements.add(createRuleStatement);
        }

        return createRuleStatements;
    }
}
