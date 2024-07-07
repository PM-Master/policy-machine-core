package gov.nist.csd.pm.common.obligation.pattern;

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

    public void addNode(String entity) {
        nodes.add(entity);
    }
}
