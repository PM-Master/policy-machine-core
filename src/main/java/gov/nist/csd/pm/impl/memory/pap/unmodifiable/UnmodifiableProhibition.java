package gov.nist.csd.pm.impl.memory.pap.unmodifiable;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;

import java.util.List;

public class UnmodifiableProhibition extends Prohibition {

    public UnmodifiableProhibition() {
    }

    public UnmodifiableProhibition(String name, ProhibitionSubject subject,
                                   AccessRightSet accessRightSet, boolean intersection,
                                   List<ContainerCondition> containers) {
        super(name, subject, new UnmodifiableAccessRightSet(accessRightSet), intersection, containers);
    }

    public UnmodifiableProhibition(Prohibition prohibition) {
        super(prohibition);
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setSubject(ProhibitionSubject subject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContainers(List<ContainerCondition> containers) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAccessRightSet(AccessRightSet accessRightSet) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIntersection(boolean intersection) {
        throw new UnsupportedOperationException();
    }
}
