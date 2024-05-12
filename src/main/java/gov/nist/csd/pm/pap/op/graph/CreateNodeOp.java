package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.node.NodeType;

import java.util.*;

public abstract class CreateNodeOp extends GraphOp {

    protected final String name;
    protected final NodeType type;
    protected final Map<String, String> properties;
    protected Collection<String> parents;

    protected CreateNodeOp(String name, NodeType type, Map<String, String> properties, Collection<String> parents) {
        this.name = name;
        this.type = type;
        this.properties = properties;
        this.parents = parents;
    }

    protected CreateNodeOp(String name, NodeType type, Map<String, String> properties) {
        this.name = name;
        this.type = type;
        this.properties = properties;
        this.parents = new ArrayList<>();
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

    public Collection<String> getParents() {
        return parents;
    }

    @Override
    public Object[] getOperands() {
        return operands(name, parents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateNodeOp that = (CreateNodeOp) o;
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
