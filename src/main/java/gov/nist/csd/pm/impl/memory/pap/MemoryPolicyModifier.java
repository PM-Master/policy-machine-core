package gov.nist.csd.pm.impl.memory.pap;

import gov.nist.csd.pm.pap.*;
import gov.nist.csd.pm.pap.modification.GraphModification;
import gov.nist.csd.pm.pap.modification.ObligationsModification;
import gov.nist.csd.pm.pap.modification.ProhibitionsModification;
import gov.nist.csd.pm.pap.modification.PMLModification;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.PMLConstantAlreadyDefinedException;
import gov.nist.csd.pm.pap.pml.value.StringValue;

import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.pap.AdminPolicy.Verifier;
import static gov.nist.csd.pm.pap.AdminPolicy.verify;
import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.common.graph.node.NodeType.PC;

public class MemoryPolicyModifier extends PolicyModifier implements BaseMemoryTx, Verifier {

    private MemoryGraphModification graph;
    private MemoryProhibitionsModification prohibitions;
    private MemoryObligationsModification obligations;
    private MemoryPMLModification userDefinedPML;

    private boolean inTx;
    private int txCounter;
    private TxPolicyModificationStore txPolicyStore;

    public MemoryPolicyModifier() throws PMException {
        this.graph = new MemoryGraphModification();
        this.prohibitions = new MemoryProhibitionsModification();
        this.obligations = new MemoryObligationsModification();
        this.userDefinedPML = new MemoryPMLModification();

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

    public void setGraph(GraphModification graphModification) {
        this.graph = (MemoryGraphModification) graphModification;
    }

    public void setProhibitions(ProhibitionsModification prohibitionsModification) {
        this.prohibitions = (MemoryProhibitionsModification) prohibitionsModification;
    }

    public void setObligations(ObligationsModification obligationsModification) {
        this.obligations = (MemoryObligationsModification) obligationsModification;
    }

    public void setUserDefinedPML(PMLModification PMLModification) {
        this.userDefinedPML = (MemoryPMLModification) PMLModification;
    }

    @Override
    public GraphModification graph() {
        return graph;
    }

    @Override
    public ProhibitionsModification prohibitions() {
        return prohibitions;
    }

    @Override
    public ObligationsModification obligations() {
        return obligations;
    }

    @Override
    public PMLModification pml() {
        return userDefinedPML;
    }

    @Override
    public void beginTx() throws PMException {
        if (!inTx) {
            txPolicyStore = new TxPolicyModificationStore(this);
        }

        inTx = true;
        txCounter++;


        graph.setTx(true, txCounter, txPolicyStore.graph());
        prohibitions.setTx(true, txCounter, txPolicyStore.prohibitions());
        obligations.setTx(true, txCounter, txPolicyStore.obligations());
        userDefinedPML.setTx(true, txCounter, txPolicyStore.pml());
    }

    @Override
    public void commit() throws PMException {
        txCounter--;
        if(txCounter != 0) {
            return;
        }

        inTx = false;
        txPolicyStore.clearOps();

        graph.tx.set(false, txCounter, txPolicyStore.graph());
        prohibitions.tx.set(false, txCounter, txPolicyStore.prohibitions());
        obligations.tx.set(false, txCounter, txPolicyStore.obligations());
        userDefinedPML.tx.set(false, txCounter, txPolicyStore.pml());
    }

    @Override
    public void rollback() throws PMException {
        inTx = false;
        txCounter = 0;

        graph.tx.set(false, txCounter, txPolicyStore.graph());
        prohibitions.tx.set(false, txCounter, txPolicyStore.prohibitions());
        obligations.tx.set(false, txCounter, txPolicyStore.obligations());
        userDefinedPML.tx.set(false, txCounter, txPolicyStore.pml());

        List<Operation> ops = txPolicyStore.txOpTracker.getOperations();
        for (Operation op : ops) {
            TxCmd txCmd = TxCmd.eventToCmd(op);
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

        txPolicyStore.clearOps();
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
