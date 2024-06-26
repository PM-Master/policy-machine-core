package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.PMLModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class PMLModificationAdjudicator implements PMLModification {
    private final UserContext userCtx;
    private final PAP pap;

    public PMLModificationAdjudicator(UserContext userCtx, PAP pap) {
        this.userCtx = userCtx;
        this.pap = pap;
    }

    @Override
    public void createFunction(FunctionDefinitionStatement functionDefinitionStatement) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName(), CREATE_FUNCTION);

        pap.modify().pml().createFunction(functionDefinitionStatement);
    }

    @Override
    public void deleteFunction(String functionName) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName(), DELETE_FUNCTION);

        pap.modify().pml().deleteFunction(functionName);
    }

    @Override
    public void createConstant(String constantName, Value constantValue) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName(), CREATE_CONSTANT);

        pap.modify().pml().createConstant(constantName, constantValue);
    }

    @Override
    public void deleteConstant(String constName) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName(), DELETE_CONSTANT);

        pap.modify().pml().deleteConstant(constName);
    }
}
