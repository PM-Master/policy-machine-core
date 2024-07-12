package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class DeassignOp extends GraphOp {

    public static final String ASCENDANT_OPERAND = "ascendant";
    public static final String DESCENTDANTS_OPERAND = "descendants";

    public DeassignOp() {
        super(
                "deassign",
                Map.of(
                        ASCENDANT_OPERAND, new RequiredCapability(DEASSIGN),
                        DESCENTDANTS_OPERAND, new RequiredCapability(DEASSIGN_FROM)
                ),
                (pap, operands) -> {
                    String asc = (String) operands.get(ASCENDANT_OPERAND);
                    List<String> descs = (List<String>) operands.get(DESCENTDANTS_OPERAND);

                    for (String desc : descs) {
                        pap.modify().graph().deassign(asc, desc);
                    }

                    return null;
                }
        );
    }
}
