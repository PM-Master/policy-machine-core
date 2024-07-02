package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.ASSOCIATE_TO;

public class DissociateOp extends GraphOp {
    private String ua;
    private String target;

    public DissociateOp() {
        super("dissociate",
              List.of(
                      new RequiredCapability("ua", List.of(DISSOCIATE)),
                      new RequiredCapability("target", List.of(DISSOCIATE_FROM))
              ));
    }

    public DissociateOp(String ua, String target, AccessRightSet accessRightSet) {
        super("dissociate",
              List.of(
                      new RequiredCapability("ua", List.of(DISSOCIATE)),
                      new RequiredCapability("target", List.of(DISSOCIATE_FROM))
              ));
        setOperands(ua, target, accessRightSet);
    }

    @Override
    public void setOperands(List<Object> operands) {
        super.setOperands(operands);

        ua = (String) operands.get(0);
        target = (String) operands.get(1);
    }

    @Override
    public Void execute(PAP pap) throws PMException {
        pap.modify().graph().dissociate(ua, target);

        return null;
    }

    public String getUa() {
        return ua;
    }

    public String getTarget() {
        return target;
    }
}
