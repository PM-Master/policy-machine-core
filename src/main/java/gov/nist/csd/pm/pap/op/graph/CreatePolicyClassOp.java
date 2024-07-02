package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_POLICY_CLASS;

public class CreatePolicyClassOp extends CreateNodeOp{
    public CreatePolicyClassOp(String name, Map<String, String> properties) {
        super("create_policy_class", name, NodeType.PC, properties, List.of(), CREATE_POLICY_CLASS);
    }

    public CreatePolicyClassOp() {
        super("create_policy_class", CREATE_POLICY_CLASS);
    }

    @Override
    public Void execute(PAP pap) throws PMException {
         pap.modify().graph().createPolicyClass(name, properties);

        return null;
    }

    @Override
    public Operation<Void> canExecute(PAP pap, UserContext userCtx) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.POLICY_CLASS_TARGETS.nodeName(), CREATE_POLICY_CLASS);

        return this;
    }
}
