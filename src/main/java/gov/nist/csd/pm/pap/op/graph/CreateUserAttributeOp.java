package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.node.NodeType;

import java.util.Collection;
import java.util.Map;

public class CreateUserAttributeOp extends CreateNodeOp{
    public CreateUserAttributeOp(String name, Map<String, String> properties, Collection<String> descendants) {
        super(name, NodeType.UA, properties, descendants);
    }

    @Override
    public String getOpName() {
        return "create_user_attribute";
    }
}