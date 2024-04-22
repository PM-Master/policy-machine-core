package gov.nist.csd.pm.common.serialization.pml;

import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;

public class PMLDeserializer implements PolicyDeserializer {

    private FunctionDefinitionStatement[] customFunctions;

    public PMLDeserializer(FunctionDefinitionStatement... customFunctions) {
        this.customFunctions = customFunctions;
    }

    public void setCustomFunctions(FunctionDefinitionStatement[] customFunctions) {
        this.customFunctions = customFunctions;
    }

    @Override
    public void deserialize(Policy policy, UserContext author, String input) throws PMException {
        PMLExecutor.compileAndExecutePML(policy, author, input, customFunctions);
    }
}
