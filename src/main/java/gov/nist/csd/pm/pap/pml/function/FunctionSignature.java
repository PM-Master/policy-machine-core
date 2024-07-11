package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.VoidValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionSignature implements PMLStatementSerializer {

    private boolean isOp;
    private String functionName;
    private Type returnType;
    private List<PMLRequiredCapability> capMap;

    public FunctionSignature(boolean isOp, String functionName, Type returnType, List<PMLRequiredCapability> capMap) {
        this.isOp = isOp;
        this.functionName = functionName;
        this.returnType = returnType;
        this.capMap = capMap;
    }

    public boolean isOp() {
        return isOp;
    }

    public void setOp(boolean op) {
        isOp = op;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public List<PMLRequiredCapability> getCapMap() {
        return capMap;
    }

    public void setCapMap(List<PMLRequiredCapability> capMap) {
        this.capMap = capMap;
    }

    @Override
    public String toFormattedString(int indentLevel) {
        String argsStr = serializeFormalArgs();

        String indent = indent(indentLevel);
        return String.format(
                "%s%s %s(%s) %s",
                indent,
                (isOp ? "operation" : "routine"),
                functionName,
                argsStr,
                returnType.isVoid() ? "" : returnType.toString() + " "
        );
    }

    private String serializeFormalArgs() {
        String pml = "";
        for (PMLRequiredCapability cap : capMap) {
            if (!pml.isEmpty()) {
                pml += ", ";
            }

            List<StringValue> arr = new ArrayList<>();
            for (String c : cap.caps()) {
                arr.add(new StringValue(c));
            }

            String capStr = "";
            if (!arr.isEmpty()) {
                ArrayValue arrayValue = new ArrayValue(new ArrayList<>(), Type.string());
                capStr = arrayValue.toString();
            }

            pml += cap.type().toString() + " " + cap.operand() + " " + (capStr.isEmpty() ? "" : capStr);
        }
        return pml;
    }
}
