package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.PreparedOperation;
import gov.nist.csd.pm.pap.op.operation.CreateAdminOperationOp;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.function.PMLOperation;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementBlock;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.Map;
import java.util.Objects;

public class CreateOperationStatement extends PreparedOperation<Void> implements CreateFunctionStatement {

    private PMLOperation op;

    public CreateOperationStatement(PMLOperation op) {
        super(new CreateAdminOperationOp(), Map.of("operation", op));

        this.op = op;
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return String.format(
                "%s%s",
                new FunctionSignature(true, op.getName(), op.getReturnType(), op.getPmlCapMap()).toFormattedString(indentLevel),
                new PMLStatementBlock(op.getStatements()).toFormattedString(indentLevel)
        );
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        super.execute(pap);

        ctx.scope().global().addFunction(op.getName(), op);

        return new VoidValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateOperationStatement that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return Objects.equals(op, that.op);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), op);
    }
}
