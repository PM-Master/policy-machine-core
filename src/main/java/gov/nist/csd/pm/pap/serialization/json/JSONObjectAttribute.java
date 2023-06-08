package gov.nist.csd.pm.pap.serialization.json;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;

import java.util.List;
import java.util.Map;

public class JSONObjectAttribute {

    private String name;
    private Map<String, String> properties;
    private List<String> assignments;

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

    public List<String> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<String> assignments) {
        this.assignments = assignments;
    }
}
