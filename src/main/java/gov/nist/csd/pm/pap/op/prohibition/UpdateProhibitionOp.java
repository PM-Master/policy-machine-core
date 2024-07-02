package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_PROCESS_PROHIBITION;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_PROHIBITION;

public class UpdateProhibitionOp extends ProhibitionOp {

    public UpdateProhibitionOp(String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                               boolean intersection, Collection<ContainerCondition> containers) {
        super("update_prohibition", name, subject, accessRightSet, intersection, containers,
              CREATE_PROCESS_PROHIBITION, CREATE_PROHIBITION);
    }

    public UpdateProhibitionOp() {
        super("update_prohibition", CREATE_PROCESS_PROHIBITION, CREATE_PROHIBITION);
    }

    @Override
    public Void execute(PAP pap) throws PMException {
        pap.modify().prohibitions().create(name, subject, accessRightSet, intersection, containers);

        return null;
    }
}
