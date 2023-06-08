package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.PMLQuerier;

import java.util.Map;

public class MemoryPMLQuerier extends PMLQuerier {

    private MemoryPolicy memoryPolicy;

    public MemoryPMLQuerier(MemoryPolicy memoryPolicy) {
        this.memoryPolicy = memoryPolicy;
    }

    @Override
    public FunctionDefinitionStatement getFunctionInternal(String name) throws PMException {
        return memoryPolicy.functions.get(name);
    }

    @Override
    public Value getConstantInternal(String name) throws PMException {
        return memoryPolicy.constants.get(name);
    }

    @Override
    public Map<String, FunctionDefinitionStatement> getFunctions() throws PMException {
        return memoryPolicy.functions;
    }

    @Override
    public Map<String, Value> getConstants() throws PMException {
        return memoryPolicy.constants;
    }
}
