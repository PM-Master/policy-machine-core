package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.common.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.pap.query.UserContext;

public class PMLBootstrapper implements PolicyBootstrapper{

    private UserContext author;
    private String pml;
    private FunctionDefinitionStatement[] customFunctions;

    public PMLBootstrapper(UserContext author, String pml, FunctionDefinitionStatement ... customFunctions) {
        this.author = author;
        this.pml = pml;
        this.customFunctions = customFunctions;
    }

    @Override
    public void bootstrap(PAP pap) throws PMException {
        pap.deserialize(author, pml, new PMLDeserializer(customFunctions));
    }
}
