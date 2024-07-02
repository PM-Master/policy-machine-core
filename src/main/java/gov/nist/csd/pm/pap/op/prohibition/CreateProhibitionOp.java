package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;

import java.util.*;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class CreateProhibitionOp extends ProhibitionOp {

    public CreateProhibitionOp(String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                               boolean intersection, Collection<ContainerCondition> containers) {
        super("create_prohibition", name, subject, accessRightSet, intersection, containers,
              CREATE_PROCESS_PROHIBITION, CREATE_PROHIBITION);
    }

    public CreateProhibitionOp() {
        super("create_prohibition", CREATE_PROCESS_PROHIBITION, CREATE_PROHIBITION);
    }

    @Override
    public Void execute(PAP pap) throws PMException {
        pap.modify().prohibitions().create(name, subject, accessRightSet, intersection, containers);

        return null;
    }
}
