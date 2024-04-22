package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.expression.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record CompiledPML(Map<String, Expression> constants, Map<String, FunctionDefinitionStatement> functions, List<PMLStatement> stmts) {

    public CompiledPML() {
        this(new HashMap<>(), new HashMap<>(), new ArrayList<>());
    }

}
