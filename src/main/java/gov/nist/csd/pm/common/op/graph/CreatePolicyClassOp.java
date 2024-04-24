package gov.nist.csd.pm.common.op.graph;

import gov.nist.csd.pm.common.graph.nodes.NodeType;

import java.util.Map;

public class CreatePolicyClassOp extends CreateNodeOp{
    public CreatePolicyClassOp(String name, Map<String, String> properties) {
        super(name, NodeType.PC, properties);
    }

    @Override
    public String getOpName() {
        return "create_policy_class";
    }
}
