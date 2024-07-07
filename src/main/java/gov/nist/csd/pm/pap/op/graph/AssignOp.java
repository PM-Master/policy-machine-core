package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN_TO;

public class AssignOp extends GraphOp {
    public AssignOp() {
        super(
                "assign",
                List.of(
                        new RequiredCapability("ascendant", List.of(ASSIGN)),
                        new RequiredCapability("descendant", List.of(ASSIGN_TO))
                ),
                (pap, operands) -> {
                    pap.modify().graph().assign((String) operands.get(0), (String) operands.get(1));

                    return null;
                }
        );
    }
}
