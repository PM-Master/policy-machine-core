package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.List;

public abstract class GraphOp extends Operation<Void> {

    public GraphOp(String opName, List<RequiredCapability> capMap) {
        super(opName, capMap);
    }
}
