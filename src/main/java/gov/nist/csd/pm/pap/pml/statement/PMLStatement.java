package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.Serializable;

public abstract class PMLStatement implements Serializable {

    private ParserRuleContext ctx;

    public PMLStatement() {}

    public PMLStatement(ParserRuleContext ctx) {
        this.ctx = ctx;
    }

    public ParserRuleContext getCtx() {
        return ctx;
    }

    public boolean hasError() {
        return ctx != null;
    }

    public abstract Value execute(ExecutionContext ctx, Policy policy) throws PMException;

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public final String toString() {
        return toFormattedString(0);
    }

    public abstract String toFormattedString(int indentLevel);

    public static String indent(int indentLevel) {
        String INDENT = "    ";
        return INDENT.repeat(indentLevel);
    }
}
