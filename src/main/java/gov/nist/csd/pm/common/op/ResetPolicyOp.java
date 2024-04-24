package gov.nist.csd.pm.common.op;

import java.io.Serial;

public class ResetPolicyOp implements Operation {

    public ResetPolicyOp() {
    }

    @Override
    public String getOpName() {
        return "reset_policy";
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
