package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;

import java.util.Collection;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.common.graph.node.NodeType.UA;

public class CreateUserAttributeOp extends CreateNodeOp{
    public CreateUserAttributeOp(String name,
                          Map<String, String> properties,
                          Collection<String> descendants) {
        super("create_user_attribute", name, UA, properties, descendants, AdminAccessRights.CREATE_OBJECT_ATTRIBUTE);

    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().createUserAttribute(name, properties, descendants);
    }
}