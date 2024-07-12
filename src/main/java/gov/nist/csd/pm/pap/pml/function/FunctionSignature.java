package gov.nist.csd.pm.pap.pml.function;

import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionSignature implements PMLStatementSerializer {

    private boolean isOp;
    private String functionName;
    private Type returnType;
    private Map<String, PMLRequiredCapability> capMap;

    public FunctionSignature(boolean isOp, String functionName, Type returnType, Map<String, PMLRequiredCapability> capMap) {
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

    public Map<String, PMLRequiredCapability> getCapMap() {
        return capMap;
    }

    public void setCapMap(Map<String, PMLRequiredCapability> capMap) {
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
        for (Map.Entry<String, PMLRequiredCapability> entry : capMap.entrySet()) {
            if (!pml.isEmpty()) {
                pml += ", ";
            }

            String key = entry.getKey();
            PMLRequiredCapability cap = entry.getValue();

            List<StringValue> arr = new ArrayList<>();
            for (String c : cap.caps()) {
                arr.add(new StringValue(c));
            }

            String capStr = "";
            if (!arr.isEmpty()) {
                ArrayValue arrayValue = new ArrayValue(new ArrayList<>(), Type.string());
                capStr = arrayValue.toString();
            }

            pml += cap.type().toString() + " " + key + " " + (capStr.isEmpty() ? "" : capStr);
        }
        return pml;
    }
}
