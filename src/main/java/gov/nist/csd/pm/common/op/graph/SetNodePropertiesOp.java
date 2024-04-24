package gov.nist.csd.pm.common.op.graph;

import gov.nist.csd.pm.common.op.Operation;

import java.io.Serial;
import java.util.Map;
import java.util.Objects;

public class SetNodePropertiesOp implements Operation {
    private final String name;
    private final Map<String, String> properties;

    public SetNodePropertiesOp(String name, Map<String, String> properties) {
        this.name = name;
        this.properties = properties;
    }

    @Override
    public String getOpName() {
        return "set_node_properties";
    }

    public String name() {
        return name;
    }

    public Map<String, String> properties() {
        return properties;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (SetNodePropertiesOp) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }

    @Override
    public String toString() {
        return "SetNodePropertiesOp[" +
                "name=" + name + ", " +
                "properties=" + properties + ']';
    }


}
