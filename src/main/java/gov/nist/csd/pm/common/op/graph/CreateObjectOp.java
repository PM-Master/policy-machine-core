package gov.nist.csd.pm.common.op.graph;

import gov.nist.csd.pm.common.graph.nodes.NodeType;

import java.util.List;
import java.util.Map;

public class CreateObjectOp extends CreateNodeOp{
    public CreateObjectOp(String name, Map<String, String> properties, List<String> parents) {
        super(name, NodeType.O, properties, parents);
    }

    @Override
    public String getOpName() {
        return "create_object";
    }
}
