package gov.nist.csd.pm.pap.op.userdefinedpml;

import java.util.Objects;

public class DeleteFunctionOp extends UserDefinedPMLOp {
    private final String functionName;

    public DeleteFunctionOp(String functionName) {
        super(operands(functionName));
        this.functionName = functionName;
    }

    @Override
    public String getOpName() {
        return "delete_function";
    }

    public String functionName() {
        return functionName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (DeleteFunctionOp) obj;
        return Objects.equals(this.functionName, that.functionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(functionName);
    }

    @Override
    public String toString() {
        return "DeleteFunctionOp[" +
                "functionName=" + functionName + ']';
    }


}
