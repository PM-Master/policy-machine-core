package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_PROCESS_PROHIBITION;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.DELETE_PROHIBITION;

public class DeleteProhibitionOp extends ProhibitionOp {


    public DeleteProhibitionOp(String name, ProhibitionSubject subject, AccessRightSet accessRightSet,
                               boolean intersection, Collection<ContainerCondition> containers) {
        super("delete_prohibition", name, subject, accessRightSet, intersection, containers,
              DELETE_PROCESS_PROHIBITION, DELETE_PROHIBITION);
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().prohibitions().delete(name);
    }

    @Override
    public String toString() {
        return "DeleteProhibitionOp{" +
                "name='" + name + '\'' +
                ", subject=" + subject +
                ", accessRightSet=" + accessRightSet +
                ", intersection=" + intersection +
                ", containers=" + containers +
                ", processReqCap='" + processReqCap + '\'' +
                ", reqCap='" + reqCap + '\'' +
                '}';
    }
}
