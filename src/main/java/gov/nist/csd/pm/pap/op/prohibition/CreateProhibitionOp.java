package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.OperationExecutor;

import java.util.*;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class CreateProhibitionOp extends ProhibitionOp {

    public CreateProhibitionOp() {
        super("create_prohibition", CREATE_PROCESS_PROHIBITION, CREATE_PROHIBITION,
              (pap, operands) -> {
                  pap.modify().prohibitions().create(
                          (String) operands.get(0),
                          (ProhibitionSubject) operands.get(1),
                          (AccessRightSet) operands.get(2),
                          (Boolean) operands.get(3),
                          (Collection<ContainerCondition>) operands.get(4)
                  );

                  return null;
              }
        );
    }
}
