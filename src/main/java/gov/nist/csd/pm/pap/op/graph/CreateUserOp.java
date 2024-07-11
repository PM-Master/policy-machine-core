package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.AdminAccessRights;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.U;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBJECT_ATTRIBUTE;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_USER;

public class CreateUserOp extends CreateNodeOp{
    public CreateUserOp() {
        super(
                "create_user",
                List.of(
                        new RequiredCapability("name"),
                        new RequiredCapability("properties"),
                        new RequiredCapability("descendants", List.of(CREATE_USER))
                ),
                (pap, operands) -> {
                    pap.modify().graph().createUser(
                            (String) operands.get(0),
                            (Map<String, String>) operands.get(1),
                            (Collection<String>) operands.get(2)
                    );

                    return null;
                }
        );
    }
}
