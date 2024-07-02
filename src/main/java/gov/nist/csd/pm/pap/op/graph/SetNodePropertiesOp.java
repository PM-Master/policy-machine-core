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
    private String name;
    private Map<String, String> properties;

    public SetNodePropertiesOp() {
        super("set_node_properties",
              List.of(
                      new RequiredCapability("node", List.of(SET_NODE_PROPERTIES)),
                      new RequiredCapability("properties", new ArrayList<>())
              ));
    }

    public SetNodePropertiesOp(String name, Map<String, String> properties) {
        super("set_node_properties",
              List.of(
                      new RequiredCapability("node", List.of(SET_NODE_PROPERTIES)),
                      new RequiredCapability("properties", new ArrayList<>())
              ));

        setOperands(name, properties);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    @Override
    public void setOperands(List<Object> operands) {
        super.setOperands(operands);

        this.name = (String) operands.get(0);
        this.properties = (Map<String, String>) operands.get(1);
    }

    @Override
    public Void execute(PAP pap) throws PMException {
        pap.modify().graph().setNodeProperties(name, properties);

        return null;
    }
}
