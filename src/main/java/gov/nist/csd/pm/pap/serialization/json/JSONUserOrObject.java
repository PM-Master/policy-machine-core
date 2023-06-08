package gov.nist.csd.pm.pap.serialization.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JSONUserOrObject {

    private String name;
    private Map<String, String> properties;
    private Collection<String> assignments;

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

    public Collection<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(Collection<String> assignments) {
        this.assignments = assignments;
    }
}
