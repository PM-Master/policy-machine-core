package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.PolicySerializer;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.epp.EventProcessor;
import gov.nist.csd.pm.pap.*;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.exception.BootstrapExistingPolicyException;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLExecutable;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.common.tx.TxRunner;
import gov.nist.csd.pm.pdp.adjudicator.PolicyModificationAdjudicator;
import gov.nist.csd.pm.pdp.adjudicator.PolicyQueryAdjudicator;
import gov.nist.csd.pm.pdp.adjudicator.PrivilegeChecker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gov.nist.csd.pm.pap.AdminPolicy.ALL_NODE_NAMES;
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

    public static class PDPTx extends PAP {

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

        @Override
        public PolicyModificationAdjudicator modify() {
            return pdpModifier;
        }

        @Override
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
        public void deserialize(UserContext author, String input, PolicyDeserializer policyDeserializer)
                throws PMException {
            PrivilegeChecker.check(pap, userCtx, AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(), DESERIALIZE_POLICY);

            pap.deserialize(author, input, policyDeserializer);
        }

        @Override
        public void executePML(UserContext userContext, String input, FunctionDefinitionStatement... functionDefinitionStatements) throws PMException {
            PMLExecutor.compileAndExecutePML(this, userContext, input, functionDefinitionStatements);
        }

        @Override
        public void executePMLFunction(UserContext userContext, String functionName, Value... values) throws PMException {
            String pml = String.format("%s(%s)", functionName, PMLExecutable.valuesToArgs(values));

            // execute function as pml
            PMLExecutor.compileAndExecutePML(this, userContext, pml);
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

}
