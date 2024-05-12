package gov.nist.csd.pm.common.serialization.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JSONUserOrObject {

    private String name;
    private Map<String, String> properties;
    private Collection<String> parents;

    public JSONUserOrObject() {
    }

    public JSONUserOrObject(String name, Map<String, String> properties, List<String> parents) {
        this.name = name;
        this.properties = properties;
        this.parents = parents;
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

    public Collection<String> getParents() {
        return parents;
    }

    public void setParents(Collection<String> parents) {
        this.parents = parents;
    }
}
