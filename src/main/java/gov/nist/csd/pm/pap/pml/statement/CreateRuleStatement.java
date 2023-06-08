package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.pattern.PatternExpression;
import gov.nist.csd.pm.pap.pml.value.*;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CreateRuleStatement extends PMLStatement {

    private Expression name;
    private PatternExpression subjectExpr;
    private PatternExpression operationExpr;
    private List<PatternExpression> operandExprs;
    private ResponseBlock responseBlock;

    public CreateRuleStatement(Expression name,
                               PatternExpression subjectExpr,
                               PatternExpression operationExpr,
                               List<PatternExpression> operandExprs,
                               ResponseBlock responseBlock) {
        this.name = name;
        this.subjectExpr = subjectExpr;
        this.operationExpr = operationExpr;
        this.operandExprs = operandExprs;
        this.responseBlock = responseBlock;
    }

    public Expression getName() {
        return name;
    }

    public PatternExpression getSubjectExpr() {
        return subjectExpr;
    }

    public PatternExpression getOperationExpr() {
        return operationExpr;
    }

    public List<PatternExpression> getOperandExprs() {
        return operandExprs;
    }

    public ResponseBlock getResponse() {
        return responseBlock;
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        StringValue nameValue = (StringValue) name.execute(ctx, pap);

        Pattern subject = subjectExpr.execute(ctx, pap).getPatternValue();
        Pattern operation = operationExpr.execute(ctx, pap).getPatternValue();
        List<Pattern> operands = new ArrayList<>();
        for (Expression operandExpr : operandExprs) {
            Value patternValue = operandExpr.execute(ctx, pap);
            operands.add(patternValue.getPatternValue());
        }

        Rule rule = new Rule(
                nameValue.getValue(),
                new EventPattern(
                        subject,
                        operation,
                        operands
                ),
                new Response(responseBlock.evtVar, responseBlock.getStatements())
        );

        return new RuleValue(rule);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        PMLStatementBlock block = new PMLStatementBlock(responseBlock.statements);

        String indent = indent(indentLevel);

        String operandsStr = "";
        for (PatternExpression operandExpr : operandExprs) {
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
                indent, subjectExpr,
                indent, operationExpr,
                operandExprs.isEmpty() ? "" : indent + "on " + operandsStr,
                indent, responseBlock.evtVar, block.toFormattedString(indentLevel)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateRuleStatement that = (CreateRuleStatement) o;
        return Objects.equals(name, that.name) && Objects.equals(
                subjectExpr, that.subjectExpr) && Objects.equals(
                operationExpr, that.operationExpr) && Objects.equals(
                operandExprs, that.operandExprs) && Objects.equals(responseBlock, that.responseBlock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subjectExpr, operationExpr, operandExprs, responseBlock);
    }

    public static class ResponseBlock implements Serializable {
        private String evtVar;
        private List<PMLStatement> statements;

        public ResponseBlock(String evtVar, List<PMLStatement> statements) {
            this.evtVar = evtVar;
            this.statements = statements;
        }

        public String getEvtVar() {
            return evtVar;
        }

        public List<PMLStatement> getStatements() {
            return statements;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ResponseBlock that = (ResponseBlock) o;
            return Objects.equals(evtVar, that.evtVar) && Objects.equals(statements, that.statements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(evtVar, statements);
        }
    }
}