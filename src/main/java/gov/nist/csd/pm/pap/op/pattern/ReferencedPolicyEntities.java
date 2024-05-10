package gov.nist.csd.pm.pap.op.pattern;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ReferencedPolicyEntities(Set<String> entities, boolean isAny) {

    public ReferencedPolicyEntities(boolean isAny) {
        this(new HashSet<>(), isAny);
    }

    public void addEntity(String entity) {
        entities.add(entity);
    }

    public void addEntities(Collection<String> entity) {
        entities.addAll(entity);
    }
}
