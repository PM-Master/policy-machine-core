package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.pap.exception.PMException;
import gov.nist.csd.pm.epp.EventProcessor;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.executable.AdminExecutable;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;
import gov.nist.csd.pm.pap.pml.PMLCompiler;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.executable.operation.PMLOperation;
import gov.nist.csd.pm.pap.pml.executable.routine.PMLRoutine;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.routine.Routine;
import gov.nist.csd.pm.pap.serialization.PolicyDeserializer;
import gov.nist.csd.pm.pap.serialization.PolicySerializer;
import gov.nist.csd.pm.pdp.modification.PolicyModificationAdjudicator;
import gov.nist.csd.pm.pdp.query.PolicyQueryAdjudicator;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class PDPTx extends PAP {

    private final UserContext userCtx;
    final PAP pap;
    private final PDPEventEmitter eventEmitter;

    private final PolicyModificationAdjudicator pdpModifier;
    private final PolicyQueryAdjudicator pdpQuerier;

    public PDPTx(UserContext userCtx, PAP pap, List<EventProcessor> epps) throws PMException {
        super(pap);

        this.userCtx = userCtx;
        this.pap = pap;
        this.eventEmitter = new PDPEventEmitter(epps);
        this.pdpModifier = new PolicyModificationAdjudicator(userCtx, pap, eventEmitter);
        this.pdpQuerier = new PolicyQueryAdjudicator(userCtx, pap);
    }

    public PolicyModificationAdjudicator modify() {
        return pdpModifier;
    }

    public PolicyQueryAdjudicator query() {
        return pdpQuerier;
    }

    @Override
    public void setPMLOperations(Map<String, PMLOperation> pmlOperations) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), SET_PML_OPS);

        super.setPMLOperations(pmlOperations);
    }

    @Override
    public void setPMLOperations(PMLOperation... operations) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), SET_PML_OPS);
        super.setPMLOperations(operations);
    }

    @Override
    public void setPMLRoutines(Map<String, PMLRoutine> pmlRoutines) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), SET_PML_ROUTINES);
        super.setPMLRoutines(pmlRoutines);
    }

    @Override
    public void setPMLRoutines(PMLRoutine... routines) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), SET_PML_ROUTINES);
        super.setPMLRoutines(routines);
    }

    @Override
    public Map<String, PMLOperation> getPMLOperations() throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), REVIEW_POLICY);
        return super.getPMLOperations();
    }

    @Override
    public Map<String, PMLRoutine> getPMLRoutines() throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), REVIEW_POLICY);
        return super.getPMLRoutines();
    }

    @Override
    public void setPMLConstants(Map<String, Value> pmlConstants) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), SET_PML_CONSTANTS);
        super.setPMLConstants(pmlConstants);
    }

    @Override
    public Map<String, Value> getPMLConstants() throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), REVIEW_POLICY);
        return super.getPMLConstants();
    }

    @Override
    public Object executeAdminExecutable(AdminExecutable<?> adminExecutable, Map<String, Object> operands) throws PMException {
        if (adminExecutable instanceof Routine<?> routine) {
            return routine.execute(this, operands);
        } else if (adminExecutable instanceof Operation<?> operation) {
            operation.canExecute(pap, userCtx, operands);
            return operation.execute(pap, operands);
        }

        return adminExecutable.execute(pap, operands);
    }

    @Override
    public void reset() throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), RESET);

        pap.reset();
    }

    @Override
    public String serialize(PolicySerializer serializer) throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), SERIALIZE_POLICY);

        return pap.serialize(serializer);
    }

    @Override
    public void deserialize(UserContext author, String input, PolicyDeserializer policyDeserializer)
            throws PMException {
        PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_OBJECT.nodeName(), DESERIALIZE_POLICY);

        pap.deserialize(author, input, policyDeserializer);
    }

    public void executePML(UserContext author, String input) throws PMException {
        PMLCompiler pmlCompiler = new PMLCompiler(pap);
        List<PMLStatement> stmts = pmlCompiler.compilePML(input);

        ExecutionContext ctx = new PDPExecutionContext(author, this);
        ctx.executeStatements(stmts, Map.of());
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