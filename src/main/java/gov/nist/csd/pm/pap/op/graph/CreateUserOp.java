package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.common.graph.node.NodeType.U;

public class CreateUserOp extends CreateNodeOp{
    public CreateUserOp(String name,
                                 Map<String, String> properties,
                                 Collection<String> descendants) {
        super("create_user", name, U, properties, descendants, AdminAccessRights.CREATE_OBJECT_ATTRIBUTE);

    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().createUserAttribute(name, properties, descendants);
    }
}
