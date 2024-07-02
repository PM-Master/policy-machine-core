package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_PROHIBITION;

public class DeleteProhibitionOp extends ProhibitionOp {


    public DeleteProhibitionOp(String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                               boolean intersection, Collection<ContainerCondition> containers) {
        super("delete_prohibition", name, subject, accessRightSet, intersection, containers,
              DELETE_PROCESS_PROHIBITION, DELETE_PROHIBITION);
    }

    public DeleteProhibitionOp() {
        super("delete_prohibition", DELETE_PROCESS_PROHIBITION, DELETE_PROHIBITION);
    }

    @Override
    public Void execute(PAP pap) throws PMException {
        pap.modify().prohibitions().delete(name);

        return null;
    }
}
