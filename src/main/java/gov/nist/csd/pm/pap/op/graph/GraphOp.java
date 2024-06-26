package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.Operation;

public abstract class GraphOp extends Operation {

    public GraphOp(String opName, Operand... operands) {
        super(opName, operands);
    }
}
