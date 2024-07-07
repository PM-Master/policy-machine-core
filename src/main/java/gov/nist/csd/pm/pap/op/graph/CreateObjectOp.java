package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.O;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBJECT;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBJECT_ATTRIBUTE;

public class CreateObjectOp extends CreateNodeOp{
    public CreateObjectOp() {
        super(
                "create_object",
                List.of(
                        new RequiredCapability("name"),
                        new RequiredCapability("type"),
                        new RequiredCapability("properties"),
                        new RequiredCapability("descendants", List.of(CREATE_OBJECT))
                ),
                (pap, operands) -> {
                    pap.modify().graph().createObject(
                            (String) operands.get(0),
                            (Map<String, String>) operands.get(1),
                            (Collection<String>) operands.get(2)
                    );

                    return null;
                }
        );

    }
}
