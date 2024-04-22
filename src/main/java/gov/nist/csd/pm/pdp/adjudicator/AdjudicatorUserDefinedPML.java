package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.UserDefinedPML;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

import static gov.nist.csd.pm.pdp.AdminAccessRights.*;

public class AdjudicatorUserDefinedPML implements UserDefinedPML {
    private final UserContext userCtx;
    private final PAP pap;
    private final PrivilegeChecker privilegeChecker;

    public AdjudicatorUserDefinedPML(UserContext userCtx, PAP pap, PrivilegeChecker privilegeChecker) {
        this.userCtx = userCtx;
        this.pap = pap;
        this.privilegeChecker = privilegeChecker;
    }

    @Override
    public void createFunction(FunctionDefinitionStatement functionDefinitionStatement) throws PMException {
        privilegeChecker.check(userCtx, AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName(), CREATE_FUNCTION);
    }

    @Override
    public void deleteFunction(String functionName) throws PMException {
        privilegeChecker.check(userCtx, AdminPolicyNode.PML_FUNCTIONS_TARGET.nodeName(), DELETE_FUNCTION);
    }

    @Override
    public Map<String, FunctionDefinitionStatement> getFunctions() throws PMException {
        return null;
    }

    @Override
    public FunctionDefinitionStatement getFunction(String name) throws PMException {
        return null;
    }

    @Override
    public void createConstant(String constantName, Value constantValue) throws PMException {
        privilegeChecker.check(userCtx, AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName(), CREATE_CONSTANT);
    }

    @Override
    public void deleteConstant(String constName) throws PMException {
        privilegeChecker.check(userCtx, AdminPolicyNode.PML_CONSTANTS_TARGET.nodeName(), DELETE_CONSTANT);
    }

    @Override
    public Map<String, Value> getConstants() throws PMException {
        return null;
    }

    @Override
    public Value getConstant(String name) {
        return null;
    }
}
