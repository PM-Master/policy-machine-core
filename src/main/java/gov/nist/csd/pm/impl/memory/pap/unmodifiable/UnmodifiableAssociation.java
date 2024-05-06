package gov.nist.csd.pm.impl.memory.pap.unmodifiable;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.relationship.Association;

public class UnmodifiableAssociation extends Association {

    public UnmodifiableAssociation(String source, String target, AccessRightSet ars) {
        super(source, target, new UnmodifiableAccessRightSet(ars));
    }

    @Override
    public void setSource(String source) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTarget(String target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAccessRightSet(AccessRightSet accessRightSet) {
        throw new UnsupportedOperationException();
    }
}
