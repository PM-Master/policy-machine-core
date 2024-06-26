package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_PROCESS_PROHIBITION;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_PROHIBITION;

public class UpdateProhibitionOp extends ProhibitionOp {

    public UpdateProhibitionOp(String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                               boolean intersection, Collection<ContainerCondition> containers) {
        super("update_prohibition", name, subject, accessRightSet, intersection, containers,
              CREATE_PROCESS_PROHIBITION, CREATE_PROHIBITION);
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().prohibitions().create(name, subject, accessRightSet, intersection, containers);
    }

    @Override
    public String getOpName() {
        return "update_prohibition";
    }

    @Override
    public String toString() {
        return "UpdateProhibitionOp{" +
                "name='" + name + '\'' +
                ", subject=" + subject +
                ", accessRightSet=" + accessRightSet +
                ", intersection=" + intersection +
                ", containers=" + containers +
                '}';
    }
}
