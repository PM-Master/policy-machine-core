package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.node.NodeType;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_USER_ATTRIBUTE;

public class DeleteUserAttributeOp extends DeleteNodeOp{
    public DeleteUserAttributeOp() {
        super("delete_user_attribute", DELETE_USER_ATTRIBUTE);
    }

    public DeleteUserAttributeOp(String name, NodeType type, Collection<String> descendants) {
        super("delete_user_attribute", name, type, descendants, DELETE_USER_ATTRIBUTE);
    }
}
