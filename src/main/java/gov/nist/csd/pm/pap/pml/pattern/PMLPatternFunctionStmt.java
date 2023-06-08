package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public abstract class PMLPatternFunctionStmt extends FunctionDefinitionStatement {

    public PMLPatternFunctionStmt(String name, List<PMLPatternArg> args) {
        super(new PatternFunctionSignature(name, args));
    }

    public abstract PMLPattern getPattern(String varName, List<Value> argValues) throws PMException;
}
