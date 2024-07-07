package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.OperationPrivilegeChecker;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.*;

public abstract class CreateNodeOp extends GraphOp {
    public CreateNodeOp(String name, List<RequiredCapability> capMap,
                        OperationPrivilegeChecker checker,
                        OperationExecutor<Void> executor) {
        super(name, capMap, checker, executor);
    }

    public CreateNodeOp(String name, List<RequiredCapability> capMap, OperationExecutor<Void> executor) {
        super(name, capMap, executor);
    }
}
