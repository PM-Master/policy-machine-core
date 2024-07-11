package gov.nist.csd.pm.pap.pml.statement;

import java.util.List;
import java.util.Objects;


public class PMLStatementBlock implements PMLStatementSerializer {

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
