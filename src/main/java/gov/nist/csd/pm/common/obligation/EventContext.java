package gov.nist.csd.pm.common.obligation;

import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Map;

public record EventContext(String user, String process, String opName, List<Object> operands) {

    @Override
    public String toString() {
        return "EventContext{" +
                "user='" + user + '\'' +
                ", process='" + process + '\'' +
                ", opName='" + opName + '\'' +
                ", operands=" + operands +
                '}';
    }
}
