package gov.nist.csd.pm.pap.op;

public class ResetPolicyEvent implements PolicyEvent{
    @Override
    public String getEventName() {
        return "reset_policy";
    }

}
