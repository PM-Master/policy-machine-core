package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class DeassignOp extends GraphOp {

    public DeassignOp() {
        super(
                "create_user",
                List.of(
                        new RequiredCapability(DEASSIGN),
                        new RequiredCapability(DEASSIGN_FROM)
                ),
                (pap, operands) -> {
                    pap.modify().graph().deassign(
                            (String) operands.get(0),
                            (String) operands.get(1)
                    );

                    return null;
                }
        );
    }
}
