package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;

import java.util.List;
import java.util.Objects;

public class DeleteProhibitionOp extends ProhibitionOp {

    public DeleteProhibitionOp(String name,
                               ProhibitionSubject subject,
                               AccessRightSet accessRightSet,
                               boolean intersection,
                               List<ContainerCondition> containers) {
        super(name, subject, accessRightSet, intersection, containers);
    }

    @Override
    public String getOpName() {
        return "delete_prohibition";
    }
}
