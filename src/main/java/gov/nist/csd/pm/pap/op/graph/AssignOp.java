package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.OperationExecutor;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN_TO;

public class AssignOp extends GraphOp {

    public AssignOp() {
        super(
                "assign",
                List.of(
                        new RequiredCapability("ascendant", List.of(ASSIGN)),
                        new RequiredCapability("descendants", List.of(ASSIGN_TO))
                ),
                (pap, operands) -> {
                    String asc = (String) operands.get(0);
                    List<String> descs = (List<String>) operands.get(1);

                    for (String desc : descs) {
                        pap.modify().graph().assign(asc, desc);
                    }

                    return null;
                }
        );
    }
}
