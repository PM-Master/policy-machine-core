package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.op.Operation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CreateProhibitionOp extends ProhibitionOp {

    public CreateProhibitionOp(String name,
                               ProhibitionSubject subject,
                               AccessRightSet accessRightSet,
                               boolean intersection,
                               Collection<ContainerCondition> containers) {
        super(name, subject, accessRightSet, intersection, containers);
    }

    @Override
    public String getOpName() {
        return "create_prohibition";
    }

    @Override
    public String toString() {
        return "CreateProhibitionOp{" +
                "name='" + name + '\'' +
                ", subject=" + subject +
                ", accessRightSet=" + accessRightSet +
                ", intersection=" + intersection +
                ", containers=" + containers +
                '}';
    }
}
