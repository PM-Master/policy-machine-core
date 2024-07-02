package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.node.NodeType;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class DeleteObjectAttributeOp extends DeleteNodeOp{
    public DeleteObjectAttributeOp() {
        super("delete_object_attribute", DELETE_OBJECT_ATTRIBUTE);
    }

    public DeleteObjectAttributeOp(String name, NodeType type, Collection<String> descendants) {
        super("delete_object_attribute", name, type, descendants, DELETE_OBJECT_ATTRIBUTE);
    }
}
