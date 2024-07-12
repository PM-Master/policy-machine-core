package gov.nist.csd.pm.pap.op;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequiredCapability {

    private final List<String> caps;

    public RequiredCapability(List<String> caps) {
        this.caps = caps;
    }

    public RequiredCapability(String ... caps) {
        this.caps = List.of(caps);
    }

    public String[] capsArray() {
        return caps.toArray(String[]::new);
    }

    public List<String> caps() {
        return caps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RequiredCapability that)) {
            return false;
        }
        return Objects.equals(caps, that.caps);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(caps);
    }

    @Override
    public String toString() {
        return "RequiredCapability{" +
                "caps=" + caps +
                '}';
    }
}
