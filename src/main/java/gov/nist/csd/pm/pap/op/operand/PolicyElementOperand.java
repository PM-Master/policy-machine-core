package gov.nist.csd.pm.pap.op.operand;

public class PolicyElementOperand extends Operand {

    private String value;
    private String reqCap;

    public PolicyElementOperand(String name, String value, String reqCap) {
        super(name, value);
        this.value = value;
        this.reqCap = reqCap;
    }

    public PolicyElementOperand(String name, Object value, String reqCap) {
        super(name, value);
        this.reqCap = reqCap;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String getReqCap() {
        return reqCap;
    }
}
