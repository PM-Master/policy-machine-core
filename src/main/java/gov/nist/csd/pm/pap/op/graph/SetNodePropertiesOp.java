package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.operand.Operand;
import gov.nist.csd.pm.pap.op.operand.PolicyElementOperand;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.SET_NODE_PROPERTIES;

public class SetNodePropertiesOp extends GraphOp {
    private final String name;
    private final Map<String, String> properties;

    public SetNodePropertiesOp(String name, Map<String, String> properties) {
        super("set_node_properties",
              new PolicyElementOperand("name", name, SET_NODE_PROPERTIES),
              new Operand("properties", properties));
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().setNodeProperties(name, properties);
    }

    @Override
    public void canExecute(PAP pap, UserContext userCtx) throws PMException {
        checkPrivilegesOnOperand(pap, userCtx, (PolicyElementOperand) operands.getFirst());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (SetNodePropertiesOp) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }

    @Override
    public String toString() {
        return "SetNodePropertiesOp[" +
                "name=" + name + ", " +
                "properties=" + properties + ']';
    }


}
