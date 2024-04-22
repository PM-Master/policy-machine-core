package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.*;
import gov.nist.csd.pm.pap.Obligations;
import gov.nist.csd.pm.pap.Prohibitions;
import gov.nist.csd.pm.pap.UserDefinedPML;
import gov.nist.csd.pm.pap.op.PolicyEvent;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.PMLConstantAlreadyDefinedException;
import gov.nist.csd.pm.pap.pml.value.StringValue;

import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.pap.AdminPolicy.Verifier;
import static gov.nist.csd.pm.pap.AdminPolicy.verify;
import static gov.nist.csd.pm.common.graph.nodes.NodeType.OA;
import static gov.nist.csd.pm.common.graph.nodes.NodeType.PC;

public class MemoryPolicyStore extends PolicyStore implements BaseMemoryTx, Verifier {

    private MemoryGraph graph;
    private MemoryProhibitions prohibitions;
    private MemoryObligations obligations;
    private MemoryUserDefinedPML userDefinedPML;

    private boolean inTx;
    private int txCounter;
    private TxPolicyStore txPolicyStore;

    public MemoryPolicyStore() throws PMException {
        this.graph = new MemoryGraph();
        this.prohibitions = new MemoryProhibitions();
        this.obligations = new MemoryObligations();
        this.userDefinedPML = new MemoryUserDefinedPML();

        initStores();

        verify(this, graph);
    }

    private void initStores() {
        this.graph.setMemoryProhibitions(prohibitions);
        this.graph.setMemoryObligations(obligations);
        this.prohibitions.setMemoryGraph(graph);
        this.obligations.setMemoryGraph(graph);
        this.userDefinedPML.setMemoryGraph(graph);
    }

    public boolean isInTx() {
        return inTx;
    }

    public void setGraph(Graph graph) {
        this.graph = (MemoryGraph) graph;
    }

    public void setProhibitions(Prohibitions prohibitions) {
        this.prohibitions = (MemoryProhibitions) prohibitions;
    }

    public void setObligations(Obligations obligations) {
        this.obligations = (MemoryObligations) obligations;
    }

    public void setUserDefinedPML(UserDefinedPML userDefinedPML) {
        this.userDefinedPML = (MemoryUserDefinedPML) userDefinedPML;
    }

    @Override
    public Graph graph() {
        return graph;
    }

    @Override
    public Prohibitions prohibitions() {
        return prohibitions;
    }

    @Override
    public Obligations obligations() {
        return obligations;
    }

    @Override
    public UserDefinedPML userDefinedPML() {
        return userDefinedPML;
    }

    @Override
    public void beginTx() throws PMException {
        if (!inTx) {
            txPolicyStore = new TxPolicyStore(this);
        }

        inTx = true;
        txCounter++;


        graph.setTx(true, txCounter, txPolicyStore.graph());
        prohibitions.setTx(true, txCounter, txPolicyStore.prohibitions());
        obligations.setTx(true, txCounter, txPolicyStore.obligations());
        userDefinedPML.setTx(true, txCounter, txPolicyStore.userDefinedPML());
    }

    @Override
    public void commit() throws PMException {
        txCounter--;
        if(txCounter != 0) {
            return;
        }

        inTx = false;
        txPolicyStore.clearEvents();

        graph.tx.set(false, txCounter, txPolicyStore.graph());
        prohibitions.tx.set(false, txCounter, txPolicyStore.prohibitions());
        obligations.tx.set(false, txCounter, txPolicyStore.obligations());
        userDefinedPML.tx.set(false, txCounter, txPolicyStore.userDefinedPML());
    }

    @Override
    public void rollback() throws PMException {
        inTx = false;
        txCounter = 0;

        graph.tx.set(false, txCounter, txPolicyStore.graph());
        prohibitions.tx.set(false, txCounter, txPolicyStore.prohibitions());
        obligations.tx.set(false, txCounter, txPolicyStore.obligations());
        userDefinedPML.tx.set(false, txCounter, txPolicyStore.userDefinedPML());

        List<PolicyEvent> events = txPolicyStore.txPolicyEventTracker.getEvents();
        for (PolicyEvent policyEvent : events) {
            TxCmd txCmd = TxCmd.eventToCmd(policyEvent);
            if (txCmd.getType() == TxCmd.Type.GRAPH) {
                txCmd.rollback(graph);
            } else if (txCmd.getType() == TxCmd.Type.PROHIBITIONS) {
                txCmd.rollback(prohibitions);
            } else if (txCmd.getType() == TxCmd.Type.OBLIGATIONS) {
                txCmd.rollback(obligations);
            } else {
                txCmd.rollback(userDefinedPML);
            }
        }

        txPolicyStore.clearEvents();
    }

    @Override
    public void reset() throws PMException {
        graph.clear();
        prohibitions.clear();
        obligations.clear();
        userDefinedPML.clear();

        verify(this, graph);
    }

    @Override
    public void verifyAdminPolicyClassNode() {
        graph.createNodeInternal(AdminPolicyNode.ADMIN_POLICY.nodeName(), PC, new HashMap<>());
    }

    @Override
    public void verifyAdminPolicyAttribute(AdminPolicyNode node, AdminPolicyNode parent) throws PMException {
        if (!graph.nodeExists(node.nodeName())) {
            graph.createNodeInternal(node.nodeName(), OA, new HashMap<>());
        }

        if (!graph.getParents(node.nodeName()).contains(parent.nodeName())) {
            graph.assignInternal(node.nodeName(), parent.nodeName());
        }
    }

    @Override
    public void verifyAdminPolicyConstant(AdminPolicyNode constant) throws PMException {
        try {
            userDefinedPML.createConstant(constant.constantName(), new StringValue(constant.nodeName()));
        } catch (PMLConstantAlreadyDefinedException e) {
            // ignore this exception as the admin policy constant already existing is not an error
        }
    }
}
