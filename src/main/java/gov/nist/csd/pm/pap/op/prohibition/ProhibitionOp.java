package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.*;

import static gov.nist.csd.pm.pap.op.PrivilegeChecker.check;

public abstract class ProhibitionOp extends Operation<Void> {

    public ProhibitionOp(String opName, String processReqCap, String reqCap, OperationExecutor<Void> operationExecutor) {
        super(
                opName,
                List.of(
                        new RequiredCapability("name"),
                        new RequiredCapability("subject"),
                        new RequiredCapability("accessRightSet"),
                        new RequiredCapability("intersection"),
                        new RequiredCapability("containers")
                ),
                (pap, userCtx, op, capMap, operands) -> {
                    ProhibitionSubject subject = (ProhibitionSubject) operands.get(1);

                    if (subject.getType() == ProhibitionSubject.Type.PROCESS) {
                        check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), processReqCap);
                    } else {
                        check(pap, userCtx, subject.getName(), reqCap);
                    }


                    // check that the user can create a prohibition for each container in the condition
                    Collection<ContainerCondition> containers = (Collection<ContainerCondition>) operands.get(4);
                    for (ContainerCondition contCond : containers) {
                        check(pap, userCtx, contCond.getName(), reqCap);

                        // there is another access right needed if the condition is a complement since it applies to a greater
                        // number of nodes
                        if (contCond.isComplement()) {
                            check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), reqCap);
                        }
                    }
                },
                operationExecutor
        );
    }
}
