package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.node.NodeType;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_USER;

public class DeleteUserOp extends DeleteNodeOp{
    public DeleteUserOp() {
        super("delete_user", DELETE_USER);
    }
}
