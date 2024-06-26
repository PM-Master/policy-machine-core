package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.operand.PolicyElementListOperand;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.OA;

public class CreateObjectAttributeOp extends CreateNodeOp{
    public CreateObjectAttributeOp(String name,
                                   Map<String, String> properties,
                                   Collection<String> descendants) {
        super("create_object_attribute", name, OA, properties, descendants, AdminAccessRights.CREATE_OBJECT_ATTRIBUTE);

    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().createObjectAttribute(name, properties, descendants);
    }
}
