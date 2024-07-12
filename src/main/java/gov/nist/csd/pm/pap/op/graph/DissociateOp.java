package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class DissociateOp extends GraphOp {

    public static final String UA_OPERAND = "ua";
    public static final String TARGET_OPERAND = "target";

    public DissociateOp() {
        super("dissociate",
              Map.of(
                      UA_OPERAND, new RequiredCapability(DISSOCIATE),
                      TARGET_OPERAND, new RequiredCapability(DISSOCIATE_FROM)
              ),
              (pap, operands) -> {
                  pap.modify().graph().dissociate((String) operands.get(UA_OPERAND), (String) operands.get(TARGET_OPERAND));

                  return null;
              }
        );
    }

}
