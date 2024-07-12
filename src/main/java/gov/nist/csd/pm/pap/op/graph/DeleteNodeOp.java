package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Map;

public abstract class DeleteNodeOp extends GraphOp {

    public static final String NAME_OPERAND = "name";
    public static final String TYPE_OPERAND = "type";
    public static final String DESCENDANTS_OPERAND = "descendants";

    public DeleteNodeOp(String name, String reqCap) {
        super(
                name,
                Map.of(
                        NAME_OPERAND, new RequiredCapability(reqCap),
                        TYPE_OPERAND, new RequiredCapability(),
                        DESCENDANTS_OPERAND, new RequiredCapability(reqCap)
                ),
                (pap, operands) -> {
                    pap.modify().graph().deleteNode(name);

                    return null;
                }
        );
    }
}
