package gov.nist.csd.pm.pap.op.graph;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.RequiredCapability;

import java.util.List;
import java.util.Objects;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class AssociateOp extends GraphOp {

    private String ua;
    private String target;
    private AccessRightSet accessRightSet;

    public AssociateOp() {
        super("associate",
              List.of(
                      new RequiredCapability("ua", List.of(ASSOCIATE)),
                      new RequiredCapability("target", List.of(ASSOCIATE_TO)),
                      new RequiredCapability("accessRightSet")
              ));
    }

    public AssociateOp(String ua, String target, AccessRightSet accessRightSet) {
        super("associate",
              List.of(
                      new RequiredCapability("ua", List.of(ASSOCIATE)),
                      new RequiredCapability("target", List.of(ASSOCIATE_TO)),
                      new RequiredCapability("accessRightSet")
              ));
        setOperands(ua, target, accessRightSet);
    }

    @Override
    public void setOperands(List<Object> operands) {
        super.setOperands(operands);

        ua = (String) operands.get(0);
        target = (String) operands.get(1);
        accessRightSet = (AccessRightSet) operands.get(2);
    }

    @Override
    public void execute(PAP pap) throws PMException {
        pap.modify().graph().associate(ua, target, accessRightSet);
    }

    public String getUa() {
        return ua;
    }

    public String getTarget() {
        return target;
    }

    public AccessRightSet getAccessRightSet() {
        return accessRightSet;
    }
}
