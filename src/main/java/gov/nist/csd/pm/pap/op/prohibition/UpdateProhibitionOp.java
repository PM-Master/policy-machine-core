package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class UpdateProhibitionOp extends ProhibitionOp {

    public UpdateProhibitionOp() {
        super("update_prohibition", CREATE_PROCESS_PROHIBITION, CREATE_PROHIBITION,
              (pap, operands) -> {
                  pap.modify().prohibitions().update(
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
