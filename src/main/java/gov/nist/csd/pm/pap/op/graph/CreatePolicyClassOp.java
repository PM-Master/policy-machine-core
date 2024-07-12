package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.*;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_OBJECT_ATTRIBUTE;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_POLICY_CLASS;

public class CreatePolicyClassOp extends CreateNodeOp{

    public CreatePolicyClassOp() {
        super(
                "create_policy_class",
                Map.of(
                        NAME_OPERAND, new RequiredCapability(),
                        PROPERTIES_OPERAND, new RequiredCapability()
                ),
                (PAP pap, UserContext userCtx, String opName, Map<String, RequiredCapability> capMap, Map<String, Object> operands) -> {
                    PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), CREATE_POLICY_CLASS);
                },

                (pap, operands) -> {
                    pap.modify().graph().createPolicyClass(
                            (String) operands.get(NAME_OPERAND),
                            (Map<String, String>) operands.get(PROPERTIES_OPERAND)
                    );

                    return null;
                }
        );
    }
}
