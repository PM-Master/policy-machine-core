package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public abstract class DeleteNodeOp extends GraphOp {
    private String name;
    private NodeType type;
    private Collection<String> descendants;

    public DeleteNodeOp(String opName, String reqCap) {
        super(opName,
              List.of(
                      new RequiredCapability("node", List.of(reqCap)),
                      new RequiredCapability("type"),
                      new RequiredCapability("descendants", List.of(reqCap))
              )
        );
    }

    public DeleteNodeOp(String opName, String name, NodeType type, Collection<String> descendants, String reqCap) {
        super(opName,
              List.of(
                      new RequiredCapability("node", List.of(reqCap)),
                      new RequiredCapability("type"),
                      new RequiredCapability("descendants", List.of(reqCap))
              ));
        setOperands(name, type, descendants);
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    public Collection<String> getDescendants() {
        return descendants;
    }

    @Override
    public void setOperands(List<Object> operands) {
        super.setOperands(operands);

        this.name = (String) operands.get(0);
        this.type = (NodeType) operands.get(1);
        this.descendants = (Collection<String>) operands.get(2);
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().deleteNode(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeleteNodeOp that = (DeleteNodeOp) o;
        return Objects.equals(name, that.name) && Objects.equals(descendants, that.descendants);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, descendants);
    }

    @Override
    public String toString() {
        return "DeleteNodeOp{" +
                "name='" + name + '\'' +
                ", descendants=" + descendants +
                '}';
    }
}
