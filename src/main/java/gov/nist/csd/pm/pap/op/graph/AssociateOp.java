package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class AssociateOp extends GraphOp {

    public AssociateOp() {
        super("associate",
              List.of(
                      new RequiredCapability("ua", List.of(ASSOCIATE)),
                      new RequiredCapability("target", List.of(ASSOCIATE_TO)),
                      new RequiredCapability("accessRightSet")
              ),
              (pap, operands) -> {
                  pap.modify().graph().associate((String) operands.get(0), (String) operands.get(1), (AccessRightSet) operands.get(2));

                  return null;
              }
        );
    }
}
