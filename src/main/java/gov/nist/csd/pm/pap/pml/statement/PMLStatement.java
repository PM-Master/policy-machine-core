package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;

import java.io.Serializable;

public abstract class PMLStatement implements Serializable {

    public PMLStatement() {}

    public abstract Value execute(ExecutionContext ctx, PolicyPoint policy) throws PMException;

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
