package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.nodes.NodeType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class CreateNodeEvent extends GraphEvent {

    protected final String name;
    protected final NodeType type;
    protected final Map<String, String> properties;
    protected List<String> parents;

    protected CreateNodeEvent(String name, NodeType type, Map<String, String> properties, List<String> parents) {
        this.name = name;
        this.type = type;
        this.properties = properties;
        this.parents = parents;
    }

    protected CreateNodeEvent(String name, NodeType type, Map<String, String> properties) {
        this.name = name;
        this.type = type;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public List<String> getParents() {
        return parents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateNodeEvent that = (CreateNodeEvent) o;
        return Objects.equals(name, that.name) && type == that.type && Objects.equals(
                properties,
                that.properties
        ) && Objects.equals(parents, that.parents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, properties, parents);
    }
}
