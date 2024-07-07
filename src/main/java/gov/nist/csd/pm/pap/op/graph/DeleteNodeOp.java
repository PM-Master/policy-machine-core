package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.OperationPrivilegeChecker;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public abstract class DeleteNodeOp extends GraphOp {
    public DeleteNodeOp(String name, String reqCap) {
        super(
                name,
                List.of(
                        new RequiredCapability("node", List.of(reqCap)),
                        new RequiredCapability("type"),
                        new RequiredCapability("descendants", List.of(reqCap))
                ),
                (pap, operands) -> {
                    pap.modify().graph().deleteNode(name);

                    return null;
                }
        );
    }
}
