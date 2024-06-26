package gov.nist.csd.pm.pap.op.operand;

import java.util.Collection;
import java.util.List;

public class PolicyElementListOperand extends Operand {

    private Collection<String> value;
    private String reqCap;

    public PolicyElementListOperand(String name, Collection<String> value, String reqCap) {
        super(name, value);
        this.value = value;
        this.reqCap = reqCap;
    }

    @Override
    public Collection<String> getValue() {
        return value;
    }

    public String getReqCap() {
        return reqCap;
    }
}
