package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

public interface PMLExecutable {

    void executePML(UserContext userContext, String input, FunctionDefinitionStatement... functionDefinitionStatements)
    throws PMException;

    void executePMLFunction(UserContext userContext, String functionName, Value ... args) throws PMException;

    static String valuesToArgs(Value[] values) {
        StringBuilder args = new StringBuilder();

        for (Value value : values) {
            if (args.length() > 0) {
                args.append(", ");
            }

            args.append(value.toString());
        }

        return args.toString();
    }
}
