package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.SET_NODE_PROPERTIES;

public class SetNodePropertiesOp extends GraphOp {

    public SetNodePropertiesOp() {
        super(
                "set_node_properties",
                List.of(
                        new RequiredCapability("node", List.of(SET_NODE_PROPERTIES)),
                        new RequiredCapability("properties", new ArrayList<>())
                ),
                (pap, operands) -> {
                    pap.modify().graph().setNodeProperties(
                            (String) operands.get(0),
                            (Map<String, String>) operands.get(1)
                    );

                    return null;
                }
        );
    }
}
