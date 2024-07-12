package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBJECT_ATTRIBUTE;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_USER_ATTRIBUTE;

public class CreateUserAttributeOp extends CreateNodeOp{
    public CreateUserAttributeOp() {
        super(
                "create_user_attribute",
                Map.of(
                        NAME_OPERAND, new RequiredCapability(),
                        PROPERTIES_OPERAND, new RequiredCapability(),
                        DESCENDANTS_OPERAND, new RequiredCapability(CREATE_USER_ATTRIBUTE)
                ),
                (pap, operands) -> {
                    pap.modify().graph().createUserAttribute(
                            (String) operands.get(NAME_OPERAND),
                            (Map<String, String>) operands.get(PROPERTIES_OPERAND),
                            (Collection<String>) operands.get(DESCENDANTS_OPERAND)
                    );

                    return null;
                }
        );

    }
}