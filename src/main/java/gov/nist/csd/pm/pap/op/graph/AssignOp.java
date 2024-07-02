package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;
import gov.nist.csd.pm.pap.query.UserContext;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSIGN_TO;

public class AssignOp extends GraphOp {

    private String ascendant;
    private String descendant;

    public AssignOp() {
        super("assign", List.of(
                new RequiredCapability("ascendant", List.of(ASSIGN)),
                new RequiredCapability("descendant", List.of(ASSIGN_TO))
        ));
    }

    public AssignOp(String ascendant, String descendant) {
        super("assign", List.of(
                new RequiredCapability("ascendant", List.of(ASSIGN)),
                new RequiredCapability("descendant", List.of(ASSIGN_TO))
        ));

        setOperands(ascendant, descendant);
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().assign((String) operands.get(0), (String) operands.get(1));
    }

    @Override
    public void setOperands(List<Object> operands) {
        super.setOperands(operands);

        ascendant = (String) operands.get(0);
        descendant = (String) operands.get(1);
    }

    public String getAscendant() {
        return ascendant;
    }

    public String getDescendant() {
        return descendant;
    }
}
