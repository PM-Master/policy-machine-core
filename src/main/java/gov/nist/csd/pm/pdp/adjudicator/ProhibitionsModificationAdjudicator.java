package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.ProhibitionsModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.op.prohibition.CreateProhibitionOp;
import gov.nist.csd.pm.pap.op.prohibition.DeleteProhibitionOp;
import gov.nist.csd.pm.pap.op.prohibition.UpdateProhibitionOp;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class ProhibitionsModificationAdjudicator extends OperationExecutor implements ProhibitionsModification {
    private final UserContext userCtx;
    private final PAP pap;
    private final EventEmitter eventEmitter;

    public ProhibitionsModificationAdjudicator(UserContext userCtx, PAP pap, EventEmitter eventEmitter) {
        this.userCtx = userCtx;
        this.pap = pap;
        this.eventEmitter = eventEmitter;
    }

    @Override
    public void create(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, Collection<ContainerCondition> containerConditions) throws PMException {
        CreateProhibitionOp op = new CreateProhibitionOp(
                name,
                subject,
                accessRightSet,
                intersection,
                containerConditions
        );

        executeOpAndEmitEvent(pap, userCtx, op, eventEmitter);
    }

    @Override
    public void update(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, Collection<ContainerCondition> containerConditions) throws PMException {
        UpdateProhibitionOp op = new UpdateProhibitionOp(
                name,
                subject,
                accessRightSet,
                intersection,
                containerConditions
        );

        executeOpAndEmitEvent(pap, userCtx, op, eventEmitter);
    }

    @Override
    public void delete(String name) throws PMException {
        Prohibition prohibition = pap.query().prohibitions().get(name);

        DeleteProhibitionOp op = new DeleteProhibitionOp(prohibition.getName(),
                                                                          prohibition.getSubject(),
                                                                          prohibition.getAccessRightSet(),
                                                                          prohibition.isIntersection(),
                                                                          prohibition.getContainers()
        );

        executeOpAndEmitEvent(pap, userCtx, op, eventEmitter);
    }
}
