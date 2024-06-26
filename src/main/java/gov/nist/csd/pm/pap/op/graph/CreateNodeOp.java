package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.operand.PolicyElementListOperand;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.*;

public abstract class CreateNodeOp extends GraphOp {

    protected String name;
    protected Map<String, String> properties;
    protected Collection<String> descendants;

    public CreateNodeOp(String opName, String name, NodeType type, Map<String, String> props,
                        Collection<String> descendants, String reqCap) {
        super(opName, List.of(
                new Operand("name", name),
                new Operand("type", type),
                new Operand("properties", props),
                new PolicyElementListOperand("descendants", descendants, reqCap)).toArray(Operand[]::new));
        this.name = name;
        this.properties = props;
        this.descendants = descendants;
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
    public void canExecute(PAP pap, UserContext userCtx) throws PMException {
        checkPrivilegesOnListOperand(pap, userCtx, (PolicyElementListOperand) operands.get(3));
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
