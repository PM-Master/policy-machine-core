package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;

public interface PMLSerializable {

    void fromPML(UserContext author, String input, FunctionDefinitionStatement... customFunctions) throws PMException;
    String toPML(boolean format) throws PMException;

}
