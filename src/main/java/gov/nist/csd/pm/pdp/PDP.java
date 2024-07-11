package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.pml.CompiledPML;
import gov.nist.csd.pm.pap.pml.PMLCompiler;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.ExecuteGlobalScope;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;
import gov.nist.csd.pm.pap.serialization.PolicyDeserializer;
import gov.nist.csd.pm.pap.serialization.PolicySerializer;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.epp.EventProcessor;
import gov.nist.csd.pm.pap.*;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.exception.BootstrapExistingPolicyException;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.common.tx.TxRunner;
import gov.nist.csd.pm.pdp.adjudicator.PolicyModificationAdjudicator;
import gov.nist.csd.pm.pdp.adjudicator.PolicyQueryAdjudicator;
import gov.nist.csd.pm.pap.op.PrivilegeChecker;

import java.util.*;

import static gov.nist.csd.pm.pap.admin.AdminPolicy.ALL_NODE_NAMES;
import static gov.nist.csd.pm.common.graph.node.NodeType.ANY;
import static gov.nist.csd.pm.common.graph.node.Properties.NO_PROPERTIES;
import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;

public class PDP implements EventEmitter, AccessAdjudication {

    protected final PAP pap;
    protected final List<EventProcessor> eventProcessors;

    public PDP(PAP pap) {
        this.pap = pap;
        this.eventProcessors = new ArrayList<>();
    }

    public void runTx(UserContext userCtx, PDPTxRunner txRunner) throws PMException {
        TxRunner.runTx(pap, () -> {
            PDPTx pdpTx = new PDPTx(userCtx, pap, eventProcessors);
            txRunner.run(pdpTx);
        });
    }

    public void bootstrap(PolicyBootstrapper bootstrapper) throws PMException {
        if(!isPolicyEmpty()) {
            throw new BootstrapExistingPolicyException();
        }

        bootstrapper.bootstrap(pap);
    }

    private boolean isPolicyEmpty() throws PMException {
        Set<String> nodes = new HashSet<>(pap.query().graph().search(ANY, NO_PROPERTIES));

        boolean prohibitionsEmpty = pap.query().prohibitions().getAll().isEmpty();
        boolean obligationsEmpty = pap.query().obligations().getAll().isEmpty();

        return (nodes.isEmpty() || (nodes.size() == ALL_NODE_NAMES.size() && nodes.containsAll(ALL_NODE_NAMES))) &&
                prohibitionsEmpty &&
                obligationsEmpty;
    }

    @Override
    public void addEventListener(EventProcessor processor) {
        eventProcessors.add(processor);
    }

    @Override
    public void removeEventListener(EventProcessor processor) {
        eventProcessors.remove(processor);
    }

    @Override
    public void emitEvent(EventContext event) throws PMException {
        for (EventProcessor listener : eventProcessors) {
            listener.processEvent(event);
        }
    }

    @Override
    public ResourceAdjudicationResponse adjudicateResourceAccess(UserContext user, Operation... operations)
            throws PMException {
        /*
        TODO
        runTx(user, (pdpTx) -> {
            for (Operation operation : operations) {
                operation.apply(pdpTx);
            }
        });*/
        throw new PMException("not yet implemented");
    }

    @Override
    public AdminAdjudicationResponse adjudicateAdminAccess(UserContext user, Operation... operations)
            throws PMException {
        // TODO
        throw new PMException("not yet implemented");
    }

    public interface PDPTxRunner {
        void run(PDPTx policy) throws PMException;
    }

    public static class PDPTx implements PolicyPoint {

        private final UserContext userCtx;
        private final PAP pap;
        private final PDPEventEmitter eventEmitter;

        private final PolicyModificationAdjudicator pdpModifier;
        private final PolicyQueryAdjudicator pdpQuerier;

        public PDPTx(UserContext userCtx, PAP pap, List<EventProcessor> epps) {
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
        public void reset() throws PMException {
            PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), RESET);

            pap.reset();
        }

        @Override
        public String serialize(PolicySerializer serializer) throws PMException {
            PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), SERIALIZE_POLICY);

            return pap.serialize(serializer);
        }

        @Override
        public void deserialize(UserContext author, Collection<String> input, PolicyDeserializer policyDeserializer)
                throws PMException {
            PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), DESERIALIZE_POLICY);

            pap.deserialize(author, input, policyDeserializer);
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

        @Override
        public void executePML(UserContext author, String input) throws PMException {
            PMLCompiler pmlCompiler = new PMLCompiler()
                    .withFunctions(pap.query().operations().getAdminOperations());
            CompiledPML compiledPML = pmlCompiler.compilePML(input);
            List<PMLStatementSerializer> stmts = compiledPML.stmts();

             // add the constants and functions to the persisted scope
            // build a global scope from the policy
            GlobalScope<Value> globalScope = new ExecuteGlobalScope()
                    .withFunctions(pmlCompiler.getFunctions());

            // execute other statements
            ExecutionContext ctx = new ExecutionContext(author, new Scope<>(globalScope))
                    .withPMLExecutor(new PDPPMLExecutor(userCtx));

            for (PMLStatementSerializer stmt : stmts) {
                ctx.getExecutor().executeStatement(ctx, this, stmt);
            }
        }
    }

}
