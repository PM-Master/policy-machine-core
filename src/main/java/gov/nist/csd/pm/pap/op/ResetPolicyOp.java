package gov.nist.csd.pm.pap.op;

public class ResetPolicyOp extends Operation {

    public ResetPolicyOp(Object[] operands) {
        super(operands);
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
