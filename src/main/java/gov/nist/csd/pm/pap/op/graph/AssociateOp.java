package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class AssociateOp extends GraphOp {

    public static final String UA_OPERAND = "ua";
    public static final String TARGET_OPERAND = "target";
    public static final String ARSET_OPERAND = "arset";

    public AssociateOp() {
        super("associate",
              Map.of(
                      UA_OPERAND, new RequiredCapability(ASSOCIATE),
                      TARGET_OPERAND, new RequiredCapability(ASSOCIATE_TO),
                      ARSET_OPERAND, new RequiredCapability()
              ),
              (pap, operands) -> {
                  pap.modify().graph().associate(
                          (String) operands.get(UA_OPERAND),
                          (String) operands.get(TARGET_OPERAND),
                          (AccessRightSet) operands.get(ARSET_OPERAND)
                  );

                  return null;
              }
        );
    }
}
