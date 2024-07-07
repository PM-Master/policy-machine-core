package gov.nist.csd.pm.pap.op.prohibition;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.PAP;

import java.util.Collection;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.CREATE_PROHIBITION;

public class DeleteProhibitionOp extends ProhibitionOp {

    public DeleteProhibitionOp() {
        super("delete_prohibition", DELETE_PROCESS_PROHIBITION, DELETE_PROHIBITION,
              (pap, operands) -> {
                  pap.modify().prohibitions().delete(
                          (String) operands.get(0)
                  );

                  return null;
              }
        );
    }
}
