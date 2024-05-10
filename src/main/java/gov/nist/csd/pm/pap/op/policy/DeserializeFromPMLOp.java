package gov.nist.csd.pm.pap.op.policy;

import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;

import java.util.Arrays;
import java.util.Objects;

public class DeserializeFromPMLOp extends Operation {
    private final UserContext author;
    private final String pml;
    private final FunctionDefinitionStatement[] customFunctions;

    public DeserializeFromPMLOp(UserContext author, String pml, FunctionDefinitionStatement... customFunctions) {
        this.author = author;
        this.pml = pml;
        this.customFunctions = customFunctions;
    }

    @Override
    public String getOpName() {
        return "deserialize_from_pml";
    }

    @Override
    public Object[] getOperands() {
        return operands(author, pml, customFunctions);
    }

    public UserContext author() {
        return author;
    }

    public String pml() {
        return pml;
    }

    public FunctionDefinitionStatement[] customFunctions() {
        return customFunctions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (DeserializeFromPMLOp) obj;
        return Objects.equals(this.author, that.author) &&
                Objects.equals(this.pml, that.pml) &&
                Arrays.equals(this.customFunctions, that.customFunctions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, pml, Arrays.hashCode(customFunctions));
    }

    @Override
    public String toString() {
        return "DeserializeFromPMLOp[" +
                "author=" + author + ", " +
                "pml=" + pml + ", " +
                "customFunctions=" + customFunctions + ']';
    }


}
