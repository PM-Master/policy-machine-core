package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.*;

import static gov.nist.csd.pm.pap.op.PrivilegeChecker.check;

public abstract class ProhibitionOp extends Operation<Void> {

    public static final String NAME_OPERAND = "name";
    public static final String SUBJECT_OPERAND = "subject";
    public static final String ARSET_OPERAND = "arset";
    public static final String INTERSECTION_OPERAND = "intersection";
    public static final String CONTAINERS_OPERAND = "containers";


    public ProhibitionOp(String opName, String processReqCap, String reqCap, OperationExecutor<Void> operationExecutor) {
        super(
                opName,
                Map.of(
                        NAME_OPERAND, new RequiredCapability(),
                        SUBJECT_OPERAND, new RequiredCapability(),
                        ARSET_OPERAND, new RequiredCapability(),
                        INTERSECTION_OPERAND, new RequiredCapability(),
                        CONTAINERS_OPERAND, new RequiredCapability()
                ),
                (pap, userCtx, op, capMap, operands) -> {
                    ProhibitionSubject subject = (ProhibitionSubject) operands.get(SUBJECT_OPERAND);

                    if (subject.getType() == ProhibitionSubject.Type.PROCESS) {
                        check(pap, userCtx, AdminPolicyNode.PROHIBITIONS_TARGET.nodeName(), processReqCap);
                    } else {
                        check(pap, userCtx, subject.getName(), reqCap);
                    }


                    // check that the user can create a prohibition for each container in the condition
                    Collection<ContainerCondition> containers = (Collection<ContainerCondition>) operands.get(CONTAINERS_OPERAND);
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
