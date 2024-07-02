package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.*;

public abstract class CreateNodeOp extends GraphOp {

    protected String name;
    protected Map<String, String> properties;
    protected Collection<String> descendants;

    public CreateNodeOp(String opName, String reqCap) {
        super(opName, List.of(
                new RequiredCapability("name"),
                new RequiredCapability("type"),
                new RequiredCapability("properties"),
                new RequiredCapability("descendants", List.of(reqCap))
        ));
    }

    public CreateNodeOp(String opName, String name, NodeType type, Map<String, String> props,
                        Collection<String> descendants, String reqCap) {
        super(opName, List.of(
                new RequiredCapability("name"),
                new RequiredCapability("type"),
                new RequiredCapability("properties"),
                new RequiredCapability("descendants", List.of(reqCap))
        ));

        setOperands(name, type, props, descendants);
    }

    @Override
    public void setOperands(List<Object> operands) {
        this.name = (String) operands.get(0);
        this.properties = (Map<String, String>) operands.get(0);
        this.descendants = (Collection<String>) operands.get(0);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public Collection<String> getDescendants() {
        return descendants;
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
        return Objects.equals(name, that.name) && Objects.equals(
                properties,
                that.properties
        ) && Objects.equals(descendants, that.descendants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, properties, descendants);
    }

}
