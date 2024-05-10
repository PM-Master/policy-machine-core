package gov.nist.csd.pm.common.serialization.pml;

import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
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
    public void deserialize(PAP pap, UserContext author, String input) throws PMException {
        PMLExecutor.compileAndExecutePML(pap, author, input, customFunctions);
    }
}
