package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.OperationPrivilegeChecker;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.List;
import java.util.Map;

public abstract class GraphOp extends Operation<Void> {

    public GraphOp(String name, Map<String, RequiredCapability> capMap, OperationExecutor<Void> executor) {
        super(name, capMap, executor);
    }

    public GraphOp(String name, Map<String, RequiredCapability> capMap, OperationPrivilegeChecker checker,
                   OperationExecutor<Void> executor) {
        super(name, capMap, checker, executor);
    }
}
