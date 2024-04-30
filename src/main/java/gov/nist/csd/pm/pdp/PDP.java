package gov.nist.csd.pm.pdp;

import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.epp.EventEmitter;
import gov.nist.csd.pm.epp.EventProcessor;
import gov.nist.csd.pm.pap.*;
import gov.nist.csd.pm.pdp.adjudicator.Adjudicator;
import gov.nist.csd.pm.pap.exception.BootstrapExistingPolicyException;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.PMLExecutable;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.PolicyReview;
import gov.nist.csd.pm.common.tx.TxRunner;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.PolicySerializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gov.nist.csd.pm.pap.AdminPolicy.ALL_NODE_NAMES;
import static gov.nist.csd.pm.common.graph.nodes.NodeType.ANY;
import static gov.nist.csd.pm.common.graph.nodes.Properties.NO_PROPERTIES;

public class PDP implements EventEmitter {

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
        Set<String> nodes = new HashSet<>(pap.policy().graph().search(ANY, NO_PROPERTIES));

        boolean prohibitionsEmpty = pap.policy().prohibitions().getAll().isEmpty();
        boolean obligationsEmpty = pap.policy().obligations().getAll().isEmpty();

        return (nodes.isEmpty() || (nodes.size() == ALL_NODE_NAMES.size() && nodes.containsAll(ALL_NODE_NAMES))) &&
                prohibitionsEmpty &&
                obligationsEmpty;
    }

    @Override
    public void addEventListener(EventProcessor listener) {
        eventProcessors.add(listener);
    }

    @Override
    public void removeEventListener(EventProcessor listener) {
        eventProcessors.remove(listener);
    }

    @Override
    public void emitEvent(EventContext event) throws PMException {
        for (EventProcessor listener : eventProcessors) {
            listener.processEvent(event);
        }
    }

    public interface PDPTxRunner {
        void run(PDPTx policy) throws PMException;
    }

    public static class PDPTx implements Policy, PMLExecutable, EventEmitter, EventProcessor {

        private final Adjudicator adjudicator;
        private final PAP pap;
        private final List<EventProcessor> epps;

        private final PDPGraph pdpGraph;
        private final PDPProhibitions pdpProhibitions;
        private final PDPObligations pdpObligations;
        private final PDPUserDefinedPML pdpUserDefinedPML;

        private final PDPReviewer pdpReviewer;

        public PDPTx(UserContext userCtx, PAP pap, List<EventProcessor> epps) {
            this.adjudicator = new Adjudicator(userCtx, pap);
            this.pap = pap;
            this.epps = epps;

            this.pdpGraph = new PDPGraph(userCtx, adjudicator.graph(), pap, this);
            this.pdpProhibitions = new PDPProhibitions(userCtx, adjudicator.prohibitions(), pap, this);
            this.pdpObligations = new PDPObligations(userCtx, adjudicator.obligations(), pap, this);
            this.pdpUserDefinedPML = new PDPUserDefinedPML(userCtx, adjudicator.userDefinedPML(), pap, this);

            this.pdpReviewer = new PDPReviewer(userCtx, pap, this.adjudicator.getAccessRightChecker());
        }

        @Override
        public void addEventListener(EventProcessor listener) {
            epps.add(listener);
        }

        @Override
        public void removeEventListener(EventProcessor listener) {
            epps.remove(listener);
        }

        @Override
        public void emitEvent(EventContext event) throws PMException {
            for (EventProcessor epp : epps) {
                epp.processEvent(event);
            }
        }

        @Override
        public void processEvent(EventContext eventCtx) throws PMException {
            for (EventProcessor epp : epps) {
                epp.processEvent(eventCtx);
            }
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
        public Graph graph() {
            return pdpGraph;
        }

        @Override
        public Prohibitions prohibitions() {
            return pdpProhibitions;
        }

        @Override
        public Obligations obligations() {
            return pdpObligations;
        }

        @Override
        public UserDefinedPML userDefinedPML() {
            return pdpUserDefinedPML;
        }

        @Override
        public String serialize(PolicySerializer policySerializer) throws PMException {
            adjudicator.serialize(policySerializer);

            return pap.policy().serialize(policySerializer);
        }

        @Override
        public void deserialize(UserContext author, String input, PolicyDeserializer policyDeserializer)
                throws PMException {
            adjudicator.deserialize(author, input, policyDeserializer);

            pap.policy().deserialize(author, input, policyDeserializer);
        }

        @Override
        public void reset() throws PMException {
            adjudicator.reset();

            pap.policy().reset();
        }
    }
}
