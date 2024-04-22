package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.value.ContinueValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;

import java.util.Objects;


public class ContinueStatement extends PMLStatement {

    public ContinueStatement() {
    }

    public ContinueStatement(PMLParser.ContinueStatementContext ctx) {
        super(ctx);
    }

    @Override
    public Value execute(ExecutionContext ctx, Policy policy) throws PMException {
        return new ContinueValue();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ContinueStatement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + "continue";
    }
}
