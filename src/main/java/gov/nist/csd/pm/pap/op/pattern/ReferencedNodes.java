package gov.nist.csd.pm.pap.op.pattern;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public record ReferencedNodes(Set<String> nodes, boolean isAny) {

    public ReferencedNodes(boolean isAny) {
        this(new HashSet<>(), isAny);
    }

    public void addNodes(Collection<String> entity) {
        nodes.addAll(entity);
    }
}
