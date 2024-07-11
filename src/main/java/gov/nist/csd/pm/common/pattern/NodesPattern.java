package gov.nist.csd.pm.common.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;

import java.util.HashSet;
import java.util.List;

public class NodesPattern extends Pattern{

    private List<String> users;

    public NodesPattern(List<String> users) {
        this.users = users;
    }

    @Override
    public boolean matches(Object value, PAP pap) throws PMException {
        if (!(value instanceof String strValue)) {
            return false;
        }

        return users.contains(strValue);
    }

    @Override
    public ReferencedNodes getReferencedNodes() {
        return new ReferencedNodes(new HashSet<>(users), false);
    }

}
