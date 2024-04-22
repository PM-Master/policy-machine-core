package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.nodes.NodeType;

import java.util.List;
import java.util.Map;

public class CreateObjectAttributeEvent extends CreateNodeEvent{
    public CreateObjectAttributeEvent(String name, Map<String, String> properties, List<String> parents) {
        super(name, NodeType.OA, properties, parents);
    }

    @Override
    public String getEventName() {
        return "create_object_attribute";
    }

}
