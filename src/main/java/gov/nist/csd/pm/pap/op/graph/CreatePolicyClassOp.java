package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.*;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_POLICY_CLASS;

public class CreatePolicyClassOp extends CreateNodeOp{

    public CreatePolicyClassOp() {
        super(
                "create_policy_class",
                List.of(

                ),
                (PAP pap, UserContext userCtx, String opName, List<RequiredCapability> capMap, List<Object> operands) -> {
                    PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), CREATE_POLICY_CLASS);
                },
                (pap, operands) -> {
                    pap.modify().graph().createPolicyClass(
                            (String) operands.get(0),
                            (Map<String, String>) operands.get(1)
                    );

                    return null;
                });
    }
}
