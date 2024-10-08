package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.pap.exception.PMException;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.admin.AdminPolicyNode;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.pap.store.PolicyStore;

import static gov.nist.csd.pm.pap.graph.node.NodeType.OA;
import static gov.nist.csd.pm.pap.graph.node.NodeType.PC;

/**
 * PolicyStore is an abstract class that outlines the expected behavior of a backend implementation.
 */
public class PolicyModifier extends Modifier implements PolicyModification, AdminPolicy.Verifier {

    private GraphModifier graphModifier;
    private ProhibitionsModifier prohibitionsModifier;
    private ObligationsModifier obligationsModifier;
    private OperationsModifier operationsModifier;
    private RoutinesModifier routinesModifier;

    public PolicyModifier(PolicyStore store) throws PMException {
        super(store);
        this.graphModifier = new GraphModifier(store);
        this.prohibitionsModifier = new ProhibitionsModifier(store);
        this.obligationsModifier = new ObligationsModifier(store);
        this.operationsModifier = new OperationsModifier(store);
        this.routinesModifier = new RoutinesModifier(store);
    }

    @Override
    public GraphModifier graph() {
        return graphModifier;
    }

    @Override
    public ProhibitionsModifier prohibitions() {
        return prohibitionsModifier;
    }

    @Override
    public ObligationsModifier obligations() {
        return obligationsModifier;
    }

    @Override
    public OperationsModifier operations() {
        return operationsModifier;
    }

    @Override
    public RoutinesModifier routines() {
        return routinesModifier;
    }

    @Override
    public void verifyAdminPolicy() throws PMException {
        String pc = AdminPolicyNode.PM_ADMIN_PC.nodeName();

        if (!store.graph().nodeExists(pc)) {
            store.graph().createNode(pc, PC);
        }

        String oa = AdminPolicyNode.PM_ADMIN_OBJECT.nodeName();
        if (!store.graph().nodeExists(oa)) {
            store.graph().createNode(oa, OA);
        }

        if (!store.graph().getAdjacentDescendants(oa).contains(pc)) {
            store.graph().createAssignment(oa, pc);
        }
    }
}
