package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.Objects;

public class EmptyLineStatement extends PMLStatement{
    @Override
    public Value execute(ExecutionContext ctx, PolicyModification policyModification) throws PMException {
        return new VoidValue();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EmptyLineStatement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public String toFormattedString(int indentLevel) {
        return "\n";
    }
}
