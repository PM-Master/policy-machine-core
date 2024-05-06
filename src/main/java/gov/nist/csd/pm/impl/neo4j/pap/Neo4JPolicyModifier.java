package gov.nist.csd.pm.impl.neo4j.pap;

import gov.nist.csd.pm.pap.*;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.PMLConstantAlreadyDefinedException;
import gov.nist.csd.pm.pap.modification.GraphModification;
import gov.nist.csd.pm.pap.modification.ObligationsModification;
import gov.nist.csd.pm.pap.modification.ProhibitionsModification;
import gov.nist.csd.pm.pap.modification.PMLModification;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import org.neo4j.graphdb.GraphDatabaseService;

import java.util.HashMap;

import static gov.nist.csd.pm.pap.AdminPolicy.verify;
import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.common.graph.node.NodeType.PC;

public class Neo4JPolicyModifier extends PolicyModifier implements AdminPolicy.Verifier {

    private Neo4jConnection neo4j;
    private final Neo4JGraphModification graph;
    private final Neo4JProhibitionsModification prohibitions;
    private final Neo4JObligationsModification obligations;
    private final Neo4JPMLModification userDefinedPML;

    public Neo4JPolicyModifier(GraphDatabaseService service) throws PMException {
        this.neo4j = new Neo4jConnection(service);

        this.graph = new Neo4JGraphModification(this.neo4j);
        this.prohibitions = new Neo4JProhibitionsModification(this.neo4j);
        this.obligations = new Neo4JObligationsModification(this.neo4j);
        this.userDefinedPML = new Neo4JPMLModification(this.neo4j);

        // create node indexes
        /*this.neo4j.runTx(tx -> {
            tx.execute("CREATE INDEX node_name_index FOR (n:NODE) ON (n.name)");
            tx.execute("CREATE INDEX prohibition_name_index FOR (n:PROHIBITION) ON (n.name)");
            tx.execute("CREATE INDEX obligation_name_index FOR (n:NODE) ON (n.name)");
        });*/

        verify(this, graph);
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
    public void reset() throws PMException {
        neo4j.runTx(tx -> {
            tx.execute("match(n) detach delete n");

            verify(this, graph);
        });
    }

    @Override
    public void beginTx() throws PMException {
        neo4j.beginTx();
    }

    @Override
    public void commit() throws PMException {
        neo4j.commit();
    }

    @Override
    public void rollback() throws PMException {
        neo4j.rollback();
    }

    @Override
    public void verifyAdminPolicyClassNode() throws PMException {
        if (!graph.nodeExists(AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName())) {
            graph.createNodeInternal(AdminPolicyNode.ADMIN_POLICY.nodeName(), PC, new HashMap<>());
        }
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
