package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.*;

import static gov.nist.csd.pm.pap.op.PrivilegeChecker.check;

public abstract class ProhibitionOp extends Operation<Void> {

    protected String name;
    protected ProhibitionSubject subject;
    protected AccessRightSet accessRightSet;
    protected boolean intersection;
    protected Collection<ContainerCondition> containers;
    protected String processReqCap;
    protected String reqCap;

    public ProhibitionOp(String opName, String processReqCap, String reqCap) {
        super(opName, List.of(
                new RequiredCapability("name"),
                new RequiredCapability("subject"),
                new RequiredCapability("accessRightSet"),
                new RequiredCapability("intersection"),
                new RequiredCapability("containers")
        ));

        this.processReqCap = processReqCap;
        this.reqCap = reqCap;
    }

    public ProhibitionOp(String opName,
                         String name,
                         ProhibitionSubject subject,
                         AccessRightSet accessRightSet,
                         boolean intersection,
                         Collection<ContainerCondition> containers,
                         String processReqCap,
                         String reqCap) {
        super(opName, List.of(
                new RequiredCapability("name"),
                new RequiredCapability("subject"),
                new RequiredCapability("accessRightSet"),
                new RequiredCapability("intersection"),
                new RequiredCapability("containers")
        ));

        this.processReqCap = processReqCap;
        this.reqCap = reqCap;

        setOperands(name, subject, accessRightSet, intersection, containers, processReqCap, reqCap);
    }

    @Override
    public void setOperands(List<Object> operands) {
        super.setOperands(operands);

        this.name = (String) operands.get(0);
        this.subject = (ProhibitionSubject) operands.get(1);
        this.accessRightSet = (AccessRightSet) operands.get(2);
        this.intersection = (boolean) operands.get(3);
        this.containers = (Collection<ContainerCondition>) operands.get(4);
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
    public Operation canExecute(PAP pap, UserContext userCtx) throws PMException {
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

        return this;
    }
}
