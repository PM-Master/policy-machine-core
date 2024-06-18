package gov.nist.csd.pm.pap.serialization.json;

import java.util.HashMap;
import java.util.Map;

public class JSONPML {
    Map<String, String> functions;
    Map<String, String> constants;

    public JSONPML() {
        functions = new HashMap<>();
        constants = new HashMap<>();
    }

    public JSONPML(Map<String, String> functions, Map<String, String> constants) {
        this.functions = functions;
        this.constants = constants;
    }

    public Map<String, String> getFunctions() {
        return functions;
    }

    public void setFunctions(Map<String, String> functions) {
        this.functions = functions;
    }

    public Map<String, String> getConstants() {
        return constants;
    }

    public void setConstants(Map<String, String> constants) {
        this.constants = constants;
    }
}
