package gov.nist.csd.pm.pap.op.pattern;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;

import java.util.HashSet;
import java.util.Set;

public record ReferencedPolicyEntities(Set<String> nodes, AccessRightSet ars, boolean isAny) {

    public ReferencedPolicyEntities() {
        this(new HashSet<>(), new AccessRightSet(), false);
    }

    public ReferencedPolicyEntities(boolean isAny) {
        this(new HashSet<>(), new AccessRightSet(), isAny);
    }

    public void addNode(String node) {
        nodes.add(node);
    }

    public void addAccessRight(String ar) {
        ars.add(ar);
    }

    public void add(ReferencedPolicyEntities toAdd) {
        nodes.addAll(toAdd.nodes);
        ars.addAll(toAdd.ars);
    }
}
