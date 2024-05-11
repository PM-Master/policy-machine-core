package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.ProhibitionsModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.prohibition.CreateProhibitionOp;
import gov.nist.csd.pm.pap.op.prohibition.UpdateProhibitionOp;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.Prohibition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;

import java.util.List;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class ProhibitionsModificationAdjudicator implements ProhibitionsModification {
    private final UserContext userCtx;
    private final PAP pap;
    private final EventEmitter eventEmitter;

    public ProhibitionsModificationAdjudicator(UserContext userCtx, PAP pap, EventEmitter eventEmitter) {
        this.userCtx = userCtx;
        this.pap = pap;
        this.eventEmitter = eventEmitter;
    }

    @Override
    public void create(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions) throws PMException {
        checkCreate(subject, containerConditions);

        pap.modify().prohibitions().create(name, subject, accessRightSet, intersection, containerConditions);

        eventEmitter.emitEvent(new EventContext(
                userCtx,
                new CreateProhibitionOp(name, subject, accessRightSet, intersection, List.of(containerConditions))
        ));
    }

    @Override
    public void update(String name, ProhibitionSubject subject, AccessRightSet accessRightSet, boolean intersection, ContainerCondition... containerConditions) throws PMException {
        checkCreate(subject, containerConditions);

        pap.modify().prohibitions().update(name, subject, accessRightSet, intersection, containerConditions);

        eventEmitter.emitEvent(new EventContext(
                userCtx,
                new UpdateProhibitionOp(name, subject, accessRightSet, intersection, List.of(containerConditions))
        ));
    }

    @Override
    public void delete(String name) throws PMException {
        Prohibition prohibition = pap.query().prohibitions().get(name);

        // check that the user can create a prohibition for the subject
        if (prohibition.getSubject().getType() == ProhibitionSubject.Type.PROCESS) {
            PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), DELETE_PROCESS_PROHIBITION);
        } else {
            PrivilegeChecker.check(pap, userCtx, prohibition.getSubject().getName(), DELETE_PROHIBITION);
        }

        // check that the user can create a prohibition for each container in the condition
        for (ContainerCondition contCond : prohibition.getContainers()) {
            PrivilegeChecker.check(pap, userCtx, contCond.getName(), DELETE_CONTAINER_FROM_PROHIBITION);

            // there is another access right needed if the condition is a complement
            if (contCond.isComplement()) {
                PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), DELETE_CONTAINER_COMPLEMENT_FROM_PROHIBITION);
            }
        }

        pap.modify().prohibitions().delete(name);

        eventEmitter.emitEvent(new EventContext(
                userCtx,
                new CreateProhibitionOp(prohibition.getName(), prohibition.getSubject(), prohibition.getAccessRightSet(),
                        prohibition.isIntersection(), prohibition.getContainers())
        ));
    }

    private void checkCreate(ProhibitionSubject subject,
                             ContainerCondition[] containerConditions) throws PMException {
        if (subject.getType() == ProhibitionSubject.Type.PROCESS) {
            PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), CREATE_PROCESS_PROHIBITION);
        } else {
            PrivilegeChecker.check(pap, userCtx, subject.getName(), CREATE_PROHIBITION);
        }


        // check that the user can create a prohibition for each container in the condition
        for (ContainerCondition contCond : containerConditions) {
            PrivilegeChecker.check(pap, userCtx, contCond.getName(), ADD_CONTAINER_TO_PROHIBITION);

            // there is another access right needed if the condition is a complement
            if (contCond.isComplement()) {
                PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), ADD_CONTAINER_COMPLEMENT_TO_PROHIBITION);
            }
        }
    }
}
