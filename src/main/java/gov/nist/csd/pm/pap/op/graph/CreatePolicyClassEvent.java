package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.nodes.NodeType;

import java.util.Map;

public class CreatePolicyClassEvent extends CreateNodeEvent{
    public CreatePolicyClassEvent(String name, Map<String, String> properties) {
        super(name, NodeType.PC, properties);
    }

    @Override
    public String getEventName() {
        return "create_policy_class";
    }

}
