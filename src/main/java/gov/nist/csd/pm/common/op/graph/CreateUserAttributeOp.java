package gov.nist.csd.pm.common.op.graph;

import gov.nist.csd.pm.common.graph.nodes.NodeType;

import java.util.List;
import java.util.Map;

public class CreateUserAttributeOp extends CreateNodeOp{
    public CreateUserAttributeOp(String name, Map<String, String> properties, List<String> parents) {
        super(name, NodeType.UA, properties, parents);
    }

    @Override
    public String getOpName() {
        return "create_user_attribute";
    }
}