package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.Collection;
import java.util.List;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_OBJECT;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_OBJECT_ATTRIBUTE;

public class DeleteObjectOp extends DeleteNodeOp {
    public DeleteObjectOp() {
        super("delete_object", DELETE_OBJECT);
    }
}
