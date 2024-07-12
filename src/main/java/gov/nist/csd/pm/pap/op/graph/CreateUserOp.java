package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_USER;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_USER_ATTRIBUTE;

public class CreateUserOp extends CreateNodeOp{
    public CreateUserOp() {
        super(
                "create_user",
                Map.of(
                        NAME_OPERAND, new RequiredCapability(),
                        PROPERTIES_OPERAND, new RequiredCapability(),
                        DESCENDANTS_OPERAND, new RequiredCapability(CREATE_USER)
                ),
                (pap, operands) -> {
                    pap.modify().graph().createUser(
                            (String) operands.get(NAME_OPERAND),
                            (Map<String, String>) operands.get(PROPERTIES_OPERAND),
                            (Collection<String>) operands.get(DESCENDANTS_OPERAND)
                    );

                    return null;
                }
        );
    }
}
