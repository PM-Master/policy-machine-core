package gov.nist.csd.pm.common.op.graph;

import gov.nist.csd.pm.common.graph.nodes.NodeType;

import java.util.List;
import java.util.Map;

public class CreateObjectAttributeOp extends CreateNodeOp{
    public CreateObjectAttributeOp(String name, Map<String, String> properties, List<String> parents) {
        super(name, NodeType.OA, properties, parents);
    }

    @Override
    public String getOpName() {
        return "create_object_attribute";
    }
}
