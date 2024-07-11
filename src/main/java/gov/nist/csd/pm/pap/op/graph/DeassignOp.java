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
                "deassign",
                List.of(
                        new RequiredCapability("ascendant", List.of(DEASSIGN)),
                        new RequiredCapability("descendants", List.of(DEASSIGN_FROM))
                ),
                (pap, operands) -> {
                    String asc = (String) operands.get(0);
                    List<String> descs = (List<String>) operands.get(1);

                    for (String desc : descs) {
                        pap.modify().graph().deassign(asc, desc);
                    }

                    return null;
                }
        );
    }
}
