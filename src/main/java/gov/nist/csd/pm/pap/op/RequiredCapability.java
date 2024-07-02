package gov.nist.csd.pm.pap.op;

import java.util.ArrayList;
import java.util.List;

public record RequiredCapability(String operand, List<String> caps) {

    public RequiredCapability(String operand) {
        this(operand, new ArrayList<>());
    }

    public String[] capsArray() {
        return caps.toArray(String[]::new);
    }

}
