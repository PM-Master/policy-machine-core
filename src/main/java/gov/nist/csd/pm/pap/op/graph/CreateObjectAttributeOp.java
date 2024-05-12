package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.node.NodeType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CreateObjectAttributeOp extends CreateNodeOp{
    public CreateObjectAttributeOp(String name,
                                   Map<String, String> properties,
                                   Collection<String> parents) {
        super(name, NodeType.OA, properties, parents);
    }

    @Override
    public String getOpName() {
        return "create_object_attribute";
    }
}
