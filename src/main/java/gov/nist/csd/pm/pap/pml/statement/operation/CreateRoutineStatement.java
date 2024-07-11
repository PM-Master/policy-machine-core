package gov.nist.csd.pm.pap.pml.statement.operation;

import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.executable.PMLRoutine;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementBlock;

import java.util.Objects;

public class CreateRoutineStatement extends CreateOperationStatement{

    private PMLRoutine op;

    public CreateRoutineStatement(PMLRoutine op) {
        super(op);

        this.op = op;
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return String.format(
                "%s%s",
                new FunctionSignature(false, op.getName(), op.getReturnType(), op.getPmlCapMap()).toFormattedString(indentLevel),
                new PMLStatementBlock(op.getStatements()).toFormattedString(indentLevel)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateRoutineStatement that)) {
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
