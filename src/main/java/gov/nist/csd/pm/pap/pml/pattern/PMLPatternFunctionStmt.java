package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.op.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class PMLPatternFunctionStmt extends FunctionDefinitionStatement {

    protected Value valueToMatch;
    protected Pattern<?> pattern;

    public PMLPatternFunctionStmt(String name, Type type, List<FormalArgument> args, Pattern<?> pattern) {
        super(name, type, args);
        this.pattern = pattern;
    }

    public Value getValueToMatch() {
        return valueToMatch;
    }

    public void setValueToMatch(Value valueToMatch) {
        this.valueToMatch = valueToMatch;
    }

    public Pattern<?> getPattern() {
        return pattern;
    }

    public void setPattern(Pattern<?> pattern) {
        this.pattern = pattern;
    }
}
