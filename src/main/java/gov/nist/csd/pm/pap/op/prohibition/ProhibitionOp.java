package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.*;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_PROCESS_PROHIBITION;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_PROHIBITION;
import static gov.nist.csd.pm.pap.op.PrivilegeChecker.check;

public abstract class ProhibitionOp extends Operation {

    protected final String name;
    protected final ProhibitionSubject subject;
    protected final AccessRightSet accessRightSet;
    protected final boolean intersection;
    protected final Collection<ContainerCondition> containers;
    protected final transient String processReqCap;
    protected final transient String reqCap;

    public ProhibitionOp(String opName,
                         String name,
                         ProhibitionSubject subject,
                         AccessRightSet accessRightSet,
                         boolean intersection,
                         Collection<ContainerCondition> containers,
                         String processReqCap,
                         String reqCap) {
        super(opName,
              new Operand("name", name),
              new Operand("subject", subject),
              new Operand("accessRightSet", accessRightSet),
              new Operand("intersection", intersection),
              new Operand("containers", containers));
        this.name = name;
        this.subject = subject;
        this.accessRightSet = accessRightSet;
        this.intersection = intersection;
        this.containers = containers;
        this.processReqCap = processReqCap;
        this.reqCap = reqCap;
    }

    public String getName() {
        return name;
    }

    public ProhibitionSubject getSubject() {
        return subject;
    }

    public AccessRightSet getAccessRightSet() {
        return accessRightSet;
    }

    public boolean isIntersection() {
        return intersection;
    }

    public Collection<ContainerCondition> getContainers() {
        return containers;
    }

    @Override
    public void canExecute(PAP pap, UserContext userCtx) throws PMException {
        if (subject.getType() == ProhibitionSubject.Type.PROCESS) {
            check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), processReqCap);
        } else {
            check(pap, userCtx, subject.getName(), reqCap);
        }


        // check that the user can create a prohibition for each container in the condition
        for (ContainerCondition contCond : containers) {
            check(pap, userCtx, contCond.getName(), reqCap);

            // there is another access right needed if the condition is a complement since it applies to a greater
            // number of nodes
            if (contCond.isComplement()) {
                check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), reqCap);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProhibitionOp that = (ProhibitionOp) o;
        return intersection == that.intersection && Objects.equals(name, that.name) && Objects.equals(
                subject,
                that.subject
        ) && Objects.equals(accessRightSet, that.accessRightSet) && Objects.equals(
                containers,
                that.containers
        ) && Objects.equals(reqCap, that.reqCap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subject, accessRightSet, intersection, containers, reqCap);
    }

    @Override
    public String toString() {
        return "ProhibitionOp{" +
                "name='" + name + '\'' +
                ", subject=" + subject +
                ", accessRightSet=" + accessRightSet +
                ", intersection=" + intersection +
                ", containers=" + containers +
                ", reqCap='" + reqCap + '\'' +
                '}';
    }
}
