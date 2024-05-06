package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.value.*;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CreateRuleStatement extends PMLStatement {

    private Expression name;
    private Expression subjectExpr;
    private Expression operationExpr;
    private Expression operandsExpr;
    private ResponseBlock responseBlock;

    public CreateRuleStatement(Expression name,
                               Expression subjectExpr,
                               Expression operationExpr,
                               Expression operandsExpr,
                               ResponseBlock responseBlock) {
        this.name = name;
        this.subjectExpr = subjectExpr;
        this.operationExpr = operationExpr;
        this.operandsExpr = operandsExpr;
        this.responseBlock = responseBlock;
    }

    public CreateRuleStatement(PMLParser.CreateRuleStatementContext ctx) {
        super(ctx);
    }

    public Expression getName() {
        return name;
    }

    public Expression getSubjectExpr() {
        return subjectExpr;
    }

    public Expression getOperationExpr() {
        return operationExpr;
    }

    public Expression getOperandsExpr() {
        return operandsExpr;
    }

    public ResponseBlock getResponseBlock() {
        return responseBlock;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyModification policyModification) throws PMException {
        StringValue nameValue = (StringValue) name.execute(ctx, policyModification);

        Pattern subject = subjectExpr.execute(ctx, policyModification).getPatternValue();
        Pattern operation = operationExpr.execute(ctx, policyModification).getPatternValue();
        List<Value> operandPatternValues = operandsExpr.execute(ctx, policyModification).getArrayValue();
        List<Pattern<Object>> operands = new ArrayList<>();
        for (Value operand : operandPatternValues) {
            operands.add(operand.getPatternValue());
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
                operandsExpr == null ? "" : indent + " on " + operandsExpr,
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
                operandsExpr, that.operandsExpr) && Objects.equals(responseBlock, that.responseBlock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subjectExpr, operationExpr, operandsExpr, responseBlock);
    }

    public static class ResponseBlock implements Serializable {
        private final String evtVar;
        private final List<PMLStatement> statements;

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