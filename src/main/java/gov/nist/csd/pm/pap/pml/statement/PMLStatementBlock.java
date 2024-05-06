package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.List;
import java.util.Objects;


public class PMLStatementBlock extends PMLStatement{

    private List<PMLStatement> stmts;

    public PMLStatementBlock(List<PMLStatement> stmts) {
        this.stmts = stmts;
    }

    public List<PMLStatement> getStmts() {
        return stmts;
    }

    public void setStmts(List<PMLStatement> stmts) {
        this.stmts = stmts;
    }

    @Override
    public Value execute(ExecutionContext ctx, PolicyModification policyModification) throws PMException {
        for (PMLStatement stmt : stmts) {
            stmt.execute(ctx, policyModification);
        }

        return new VoidValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PMLStatementBlock that = (PMLStatementBlock) o;
        return Objects.equals(stmts, that.stmts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stmts);
    }

    @Override
    public String toFormattedString(int indentLevel) {
        StringBuilder sb = new StringBuilder("{\n");
        for (PMLStatement stmt : stmts) {
            sb.append(stmt.toFormattedString(indentLevel+1)).append("\n");
        }

        return sb.append(indent(indentLevel)).append("}").toString();
    }
}
