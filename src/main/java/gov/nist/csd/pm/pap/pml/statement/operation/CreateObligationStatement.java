package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.pattern.Pattern;
import gov.nist.csd.pm.pap.op.obligation.CreateObligationOp;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.pattern2.PMLPattern;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementBlock;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.obligation.ObligationOp.*;


public class CreateObligationStatement extends OperationStatement {

    private Expression name;
    private List<CreateRuleStatement> ruleStmts;

    public CreateObligationStatement(Expression name, List<CreateRuleStatement> ruleStmts) {
        super(new CreateObligationOp());
        this.name = name;
        this.ruleStmts = ruleStmts;
    }

    @Override
    public Map<String, Object> prepareOperands(ExecutionContext ctx, PAP pap)
            throws PMException {
        UserContext author = ctx.author();
        String nameStr = ctx.executeStatement(pap, name).getStringValue();

        // execute the create rule statements and add to obligation
        List<Rule> rules = new ArrayList<>();
        for (CreateRuleStatement createRuleStmt : ruleStmts) {
            Rule rule = createRuleFromStmt(ctx, createRuleStmt, pap);
            rules.add(rule);
        }

        return Map.of(AUTHOR_OPERAND, author, NAME_OPERAND, nameStr, RULES_OPERAND, rules);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        StringBuilder sb = new StringBuilder();
        for (CreateRuleStatement createRuleStatement : ruleStmts) {
            sb.append(ruleToFormattedString(createRuleStatement, indentLevel+1)).append("\n");
        }

        String indent = indent(indentLevel);
        return String.format(
                """
                %screate obligation %s {
                %s%s}""", indent, name, sb, indent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateObligationStatement that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(name, that.name) && Objects.equals(ruleStmts, that.ruleStmts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, ruleStmts);
    }

    private Rule createRuleFromStmt(ExecutionContext ctx, CreateRuleStatement createRuleStatement, PAP pap) throws PMException {
        String nameValue = ctx.executeStatement(pap, createRuleStatement.name).getStringValue();
        Pattern subject = ctx.executeStatement(pap, createRuleStatement.subjectExpr.getExpression()).getPatternValue();

        List<Value> operation = ctx.executeStatement(pap, createRuleStatement.operationExpr).getArrayValue();
        List<String> operations = new ArrayList<>();
        for (Value v : operation) {
            operations.add(v.getStringValue());
        }

        List<Pattern> operands = new ArrayList<>();
        for (PMLPattern operandExpr : createRuleStatement.operandExprs) {
            Value patternValue = ctx.executeStatement(pap, operandExpr.getExpression());
            operands.add(patternValue.getPatternValue());
        }

        return new Rule(
                nameValue,
                new EventPattern(
                        subject,
                        operations,
                        operands
                ),
                new Response(createRuleStatement.responseBlock.evtVar, createRuleStatement.responseBlock.getStatements())
        );
    }

    private String ruleToFormattedString(CreateRuleStatement stmt, int indentLevel) {

            PMLStatementBlock block = new PMLStatementBlock(stmt.responseBlock.statements);

            String indent = indent(indentLevel);

            String operandsStr = "";
            for (PMLPattern operandExpr : stmt.operandExprs) {
                operandsStr += operandExpr + "\n";
            }
            operandsStr = operandsStr.trim();

            return String.format(
                    """
                    %screate rule %s
                    %swhen %s
                    %sperforms %s
                    %s
                    %sdo (%s) %s""",
                    indent, name,
                    indent, stmt.subjectExpr,
                    indent, stmt.operationExpr,
                    stmt.operandExprs.isEmpty() ? "" : indent + "on " + operandsStr,
                    indent, stmt.responseBlock.evtVar, block.toFormattedString(indentLevel)
            );
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

            List<Expression> operandExprs = new ArrayList<>();
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

    private static List<Expression> getExpressions(Expression name, PMLPattern subjectExpr, Expression operationExpr,
                                                   List<PMLPattern> operandExprs) {
        List<Expression> expressions = new ArrayList<>();

        expressions.add(name);
        expressions.addAll(subjectExpr.getExpressions());
        expressions.add(operationExpr);
        operandExprs.forEach(p -> expressions.addAll(p.getExpressions()));

        return expressions;
    }


}
