package gov.nist.csd.pm.impl.neo4j.pap;

import gov.nist.csd.pm.pap.*;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.PMLConstantAlreadyDefinedException;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import org.neo4j.graphdb.GraphDatabaseService;

import java.util.HashMap;

import static gov.nist.csd.pm.pap.AdminPolicy.verify;
import static gov.nist.csd.pm.common.graph.nodes.NodeType.OA;
import static gov.nist.csd.pm.common.graph.nodes.NodeType.PC;

public class Neo4jPolicyStore extends PolicyStore implements AdminPolicy.Verifier {

    private Neo4jConnection neo4j;
    private final Neo4JGraph graph;
    private final Neo4JProhibitions prohibitions;
    private final Neo4JObligations obligations;
    private final Neo4JUserDefinedPML userDefinedPML;

    public Neo4jPolicyStore(GraphDatabaseService service) throws PMException {
        this.neo4j = new Neo4jConnection(service);

        this.graph = new Neo4JGraph(this.neo4j);
        this.prohibitions = new Neo4JProhibitions(this.neo4j);
        this.obligations = new Neo4JObligations(this.neo4j);
        this.userDefinedPML = new Neo4JUserDefinedPML(this.neo4j);

        // create node indexes
        /*this.neo4j.runTx(tx -> {
            tx.execute("CREATE INDEX node_name_index FOR (n:NODE) ON (n.name)");
            tx.execute("CREATE INDEX prohibition_name_index FOR (n:PROHIBITION) ON (n.name)");
            tx.execute("CREATE INDEX obligation_name_index FOR (n:NODE) ON (n.name)");
        });*/

        verify(this, graph);
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
