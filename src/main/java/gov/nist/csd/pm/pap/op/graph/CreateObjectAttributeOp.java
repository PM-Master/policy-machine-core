package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;

import java.util.Collection;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBJECT_ATTRIBUTE;

public class CreateObjectAttributeOp extends CreateNodeOp{
    public CreateObjectAttributeOp(String name,
                                   Map<String, String> properties,
                                   Collection<String> descendants) {
        super("create_object_attribute", name, OA, properties, descendants, CREATE_OBJECT_ATTRIBUTE);

    }

    public CreateObjectAttributeOp() {
        super("create_object_attribute", CREATE_OBJECT_ATTRIBUTE);
    }

    @Override
    public Void execute(PAP pap) throws PMException {
        pap.modify().graph().createObjectAttribute(name, properties, descendants);

        return null;
    }
}
