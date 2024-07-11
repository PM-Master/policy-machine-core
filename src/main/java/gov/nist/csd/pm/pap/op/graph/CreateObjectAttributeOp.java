package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBJECT_ATTRIBUTE;

public class CreateObjectAttributeOp extends CreateNodeOp{
    public CreateObjectAttributeOp() {
        super(
                "create_object_attribute",
                List.of(
                        new RequiredCapability("name"),
                        new RequiredCapability("properties"),
                        new RequiredCapability("descendants", List.of(CREATE_OBJECT_ATTRIBUTE))
                ),
                (pap, operands) -> {
                    pap.modify().graph().createObjectAttribute(
                            (String) operands.get(0),
                            (Map<String, String>) operands.get(1),
                            (Collection<String>) operands.get(2)
                    );

                    return null;
                }
                );

    }
}
