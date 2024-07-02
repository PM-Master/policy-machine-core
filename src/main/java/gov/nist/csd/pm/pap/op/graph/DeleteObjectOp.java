package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.node.NodeType;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_OBJECT;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_OBJECT_ATTRIBUTE;

public class DeleteObjectOp extends DeleteNodeOp{
    public DeleteObjectOp() {
        super("delete_object", DELETE_OBJECT);
    }

    public DeleteObjectOp(String name, NodeType type, Collection<String> descendants) {
        super("delete_object", name, type, descendants, DELETE_OBJECT);
    }
}
