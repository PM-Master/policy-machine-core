package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.List;
import java.util.Map;

public abstract class GraphOp extends Operation {

    public GraphOp(String opName, List<RequiredCapability> capMap) {
        super(opName, capMap);
    }
}
