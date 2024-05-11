package gov.nist.csd.pm.pap.op.pml;

import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;

import java.util.Objects;

public class CreateFunctionOp extends PMLOp {
    private final FunctionDefinitionStatement functionDefinitionStatement;

    public CreateFunctionOp(FunctionDefinitionStatement functionDefinitionStatement) {
        this.functionDefinitionStatement = functionDefinitionStatement;
    }

    @Override
    public String getOpName() {
        return "create_function";
    }

    @Override
    public Object[] getOperands() {
        return operands(functionDefinitionStatement);
    }

    public FunctionDefinitionStatement functionDefinitionStatement() {
        return functionDefinitionStatement;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (CreateFunctionOp) obj;
        return Objects.equals(this.functionDefinitionStatement, that.functionDefinitionStatement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionDefinitionStatement);
    }

    @Override
    public String toString() {
        return "CreateFunctionOp[" +
                "functionDefinitionStatement=" + functionDefinitionStatement + ']';
    }

}
