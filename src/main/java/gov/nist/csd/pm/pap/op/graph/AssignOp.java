package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN_TO;

public class AssignOp extends GraphOp {

    public static final String ASCENDANT_OPERAND = "ascendant";
    public static final String DESCENDANT_OPERAND = "descendant";

    public AssignOp() {
        super(
                "assign",
                Map.of(
                        ASCENDANT_OPERAND, new RequiredCapability(ASSIGN),
                        DESCENDANT_OPERAND, new RequiredCapability(ASSIGN_TO)
                ),
                (pap, operands) -> {
                    String asc = (String) operands.get(ASCENDANT_OPERAND);
                    List<String> descs = (List<String>) operands.get(DESCENDANT_OPERAND);

                    for (String desc : descs) {
                        pap.modify().graph().assign(asc, desc);
                    }

                    return null;
                }
        );
    }
}
