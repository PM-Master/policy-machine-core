package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.nodes.NodeType;

import java.util.List;
import java.util.Map;

public class CreateUserEvent extends CreateNodeEvent{
    public CreateUserEvent(String name, Map<String, String> properties, List<String> parents) {
        super(name, NodeType.U, properties, parents);
    }

    @Override
    public String getEventName() {
        return "create_user";
    }

}
