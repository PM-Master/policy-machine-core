package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;

public class DeserializeFromPMLEvent implements PolicyEvent{

    private final UserContext author;
    private final String pml;
    private final FunctionDefinitionStatement[] customFunctions;

    public DeserializeFromPMLEvent(UserContext author, String pml, FunctionDefinitionStatement ... customFunctions) {
        this.author = author;
        this.pml = pml;
        this.customFunctions = customFunctions;
    }

    @Override
    public String getEventName() {
        return "deserialize_from_pml";
    }

}
