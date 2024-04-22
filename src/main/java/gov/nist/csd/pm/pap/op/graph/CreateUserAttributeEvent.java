package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.nodes.NodeType;

import java.util.List;
import java.util.Map;

public class CreateUserAttributeEvent extends CreateNodeEvent{
    public CreateUserAttributeEvent(String name, Map<String, String> properties, List<String> parents) {
        super(name, NodeType.UA, properties, parents);
    }

    @Override
    public String getEventName() {
        return "create_user_attribute";
    }

}
