package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.PolicySerializer;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class Adjudicator implements PolicyModification {

    private final UserContext userCtx;
    private final PrivilegeChecker privilegeChecker;
    private final PAP pap;
    private final AdjudicatorGraphModification adjudicatorGraph;
    private final AdjudicatorProhibitionsModification adjudicatorProhibitions;
    private final AdjudicatorObligationsModification adjudicatorObligations;
    private final AdjudicatorPMLModification adjudicatorUserDefinedPML;

    public Adjudicator(UserContext userCtx, PAP pap) {
        this.userCtx = userCtx;
        this.privilegeChecker = new PrivilegeChecker(pap);
        this.pap = pap;
        this.adjudicatorGraph = new AdjudicatorGraphModification(userCtx, pap, privilegeChecker);
        this.adjudicatorProhibitions = new AdjudicatorProhibitionsModification(userCtx, pap, privilegeChecker);
        this.adjudicatorObligations = new AdjudicatorObligationsModification(userCtx, pap, privilegeChecker);
        this.adjudicatorUserDefinedPML = new AdjudicatorPMLModification(userCtx, pap, privilegeChecker);
    }

    public PrivilegeChecker getAccessRightChecker() {
        return privilegeChecker;
    }

    protected PAP getPAP() {
        return pap;
    }

    @Override
    public AdjudicatorGraphModification graph() {
        return adjudicatorGraph;
    }

    @Override
    public AdjudicatorProhibitionsModification prohibitions() {
        return adjudicatorProhibitions;
    }

    @Override
    public AdjudicatorObligationsModification obligations() {
        return adjudicatorObligations;
    }

    @Override
    public AdjudicatorPMLModification pml() {
        return adjudicatorUserDefinedPML;
    }

    @Override
    public String serialize(PolicySerializer serializer) throws PMException {
        privilegeChecker.check(userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), SERIALIZE_POLICY);

        return null;
    }

    @Override
    public void deserialize(UserContext author, String input, PolicyDeserializer policyDeserializer)
            throws PMException {
        privilegeChecker.check(userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), DESERIALIZE_POLICY);
    }

    @Override
    public void reset() throws PMException {
        privilegeChecker.check(userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), RESET);
    }
}
