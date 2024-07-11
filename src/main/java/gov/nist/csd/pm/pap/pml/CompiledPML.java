package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;
import gov.nist.csd.pm.pap.pml.expression.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record CompiledPML(Map<String, Expression> constants, Map<String, FunctionDefinitionStatement> functions, List<PMLStatementSerializer> stmts) {

    public CompiledPML() {
        this(new HashMap<>(), new HashMap<>(), new ArrayList<>());
    }

}
