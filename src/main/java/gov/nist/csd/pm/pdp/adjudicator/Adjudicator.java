package gov.nist.csd.pm.pdp.adjudicator;

import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.pap.query.PolicyQuery;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.PolicySerializer;
import gov.nist.csd.pm.pdp.PDPEventEmitter;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class Adjudicator extends PAP {

    private final UserContext userCtx;
    private final PAP pap;

    private final PolicyModificationAdjudicator modifier;
    private final PolicyQueryAdjudicator query;

    public Adjudicator(UserContext userCtx, PAP pap, PDPEventEmitter eventEmitter) {
        this.userCtx = userCtx;
        this.pap = pap;
        this.modifier = new PolicyModificationAdjudicator(userCtx, pap, eventEmitter);
        this.query = new PolicyQueryAdjudicator(userCtx, pap);
    }

    @Override
    public PolicyModification modify() {
        return modifier;
    }

    @Override
    public PolicyQuery query() {
        return query;
    }

    @Override
    public String serialize(PolicySerializer serializer) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), SERIALIZE_POLICY);

        return pap.serialize(serializer);
    }

    @Override
    public void deserialize(UserContext author, String input, PolicyDeserializer policyDeserializer)
            throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), DESERIALIZE_POLICY);

        pap.deserialize(author, input, policyDeserializer);
    }

    @Override
    public void reset() throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), RESET);

        pap.reset();
    }

    @Override
    public void beginTx() throws PMException {
        pap.beginTx();
    }

    @Override
    public void commit() throws PMException {
        pap.commit();
    }

    @Override
    public void rollback() throws PMException {
        pap.rollback();
    }
}
