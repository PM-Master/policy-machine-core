package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.value.BreakValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Objects;


public class BreakStatement extends PMLStatement {

    public BreakStatement() {
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
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
