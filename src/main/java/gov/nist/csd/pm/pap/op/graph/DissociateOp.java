package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSOCIATE_TO;

public class DissociateOp extends GraphOp {

    public DissociateOp() {
        super("dissociate",
              List.of(
                      new RequiredCapability("ua", List.of(DISSOCIATE)),
                      new RequiredCapability("target", List.of(DISSOCIATE_FROM))
              ),
              (pap, operands) -> {
                  pap.modify().graph().dissociate((String) operands.get(0), (String) operands.get(1));

                  return null;
              }
        );
    }

}
