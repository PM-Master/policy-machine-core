package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.pattern2.PMLPattern;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;


public class CreateRuleStatement {

    protected Expression name;
    protected PMLPattern subjectExpr;
    protected Expression operationExpr;
    protected List<PMLPattern> operandExprs;
    protected ResponseBlock responseBlock;

    public CreateRuleStatement(Expression name,
                               PMLPattern subjectExpr,
                               Expression operationExpr,
                               List<PMLPattern> operandExprs,
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

    public PMLPattern getSubjectExpr() {
        return subjectExpr;
    }

    public Expression getOperationExpr() {
        return operationExpr;
    }

    public List<PMLPattern> getOperandExprs() {
        return operandExprs;
    }

    public ResponseBlock getResponse() {
        return responseBlock;
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
        return Objects.equals(name, that.name) &&
                Objects.equals(subjectExpr, that.subjectExpr) &&
                Objects.equals(operationExpr, that.operationExpr) &&
                Objects.equals(operandExprs, that.operandExprs) &&
                Objects.equals(responseBlock, that.responseBlock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subjectExpr, operationExpr, operandExprs, responseBlock);
    }

    public static class ResponseBlock implements Serializable {
        protected String evtVar;
        protected List<PMLStatement> statements;

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