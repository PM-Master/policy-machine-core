package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.SET_NODE_PROPERTIES;

public class SetNodePropertiesOp extends GraphOp {

    public static final String NAME_OPERAND = "name";
    public static final String PROPERTIES_OPERAND = "properties";

    public SetNodePropertiesOp() {
        super(
                "set_node_properties",
                Map.of(
                        NAME_OPERAND, new RequiredCapability(SET_NODE_PROPERTIES),
                        PROPERTIES_OPERAND, new RequiredCapability()
                ),
                (pap, operands) -> {
                    pap.modify().graph().setNodeProperties(
                            (String) operands.get(NAME_OPERAND),
                            (Map<String, String>) operands.get(PROPERTIES_OPERAND)
                    );

                    return null;
                }
        );
    }
}
