package gov.nist.csd.pm.pap.op.prohibition;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

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
