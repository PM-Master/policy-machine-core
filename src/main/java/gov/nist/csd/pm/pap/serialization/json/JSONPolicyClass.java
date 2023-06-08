package gov.nist.csd.pm.pap.serialization.json;

import java.util.List;
import java.util.Map;

public class JSONPolicyClass {

    private String name;
    private Map<String, String> properties;

    public JSONPolicyClass() {

    }

    public JSONPolicyClass(String name, Map<String, String> properties) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
