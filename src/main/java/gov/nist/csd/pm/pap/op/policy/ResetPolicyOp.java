package gov.nist.csd.pm.pap.op.policy;

import gov.nist.csd.pm.pap.op.Operation;

public class ResetPolicyOp extends Operation {

    @Override
    public String getOpName() {
        return "reset_policy";
    }

    @Override
    public Object[] getOperands() {
        return new Object[]{};
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass() == this.getClass();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "ResetPolicyOp[]";
    }


}
