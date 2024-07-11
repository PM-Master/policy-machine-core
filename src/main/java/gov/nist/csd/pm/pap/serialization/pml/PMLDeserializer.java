package gov.nist.csd.pm.pap.serialization.pml;

import gov.nist.csd.pm.pap.serialization.PolicyDeserializer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;

public class PMLDeserializer implements PolicyDeserializer {

    private FunctionDefinitionStatement[] customFunctions;

    public PMLDeserializer(FunctionDefinitionStatement... customFunctions) {
        this.customFunctions = customFunctions;
    }

    public void setCustomFunctions(FunctionDefinitionStatement[] customFunctions) {
        this.customFunctions = customFunctions;
    }

    @Override
    public void deserialize(PAP pap, UserContext author, String input) throws PMException {
        // TODO operations and routines
        new PMLExecutor().compileAndExecutePML(pap, author, input);
    }
}
