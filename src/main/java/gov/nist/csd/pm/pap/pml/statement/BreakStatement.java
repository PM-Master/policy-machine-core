package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.value.BreakValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Objects;


public class BreakStatement extends PMLStatement {

    public BreakStatement() {
    }

    public BreakStatement(ParserRuleContext ctx) {
        super(ctx);
    }

    @Override
    public Value execute(ExecutionContext ctx, Policy policy) throws PMException {
        return new BreakValue();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BreakStatement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }


    @Override
    public String toFormattedString(int indentLevel) {
        return indent(indentLevel) + "break";
    }
}
