package gov.nist.csd.pm.pap.neo4j;

import gov.nist.csd.pm.pap.*;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.exceptions.PMLConstantAlreadyDefinedException;
import gov.nist.csd.pm.policy.pml.value.StringValue;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.pap.AdminPolicy.verify;
import static gov.nist.csd.pm.policy.model.graph.nodes.NodeType.OA;
import static gov.nist.csd.pm.policy.model.graph.nodes.NodeType.PC;

public class Neo4jPolicyStore extends PolicyStore implements AdminPolicy.Verifier {

    private Neo4jConnection neo4j;
    private final Neo4jGraphStore graph;
    private final Neo4jProhibitionsStore prohibitions;
    private final Neo4jObligationsStore obligations;
    private final Neo4jUserDefinedPMLStore userDefinedPML;

    public Neo4jPolicyStore(GraphDatabaseService service) throws PMException {
        this.neo4j = new Neo4jConnection(service);

        this.graph = new Neo4jGraphStore(this.neo4j);
        this.prohibitions = new Neo4jProhibitionsStore(this.neo4j);
        this.obligations = new Neo4jObligationsStore(this.neo4j);
        this.userDefinedPML = new Neo4jUserDefinedPMLStore(this.neo4j);

        verify(this, graph);
    }

    @Override
    public GraphStore graph() {
        return graph;
    }

    @Override
    public ProhibitionsStore prohibitions() {
        return prohibitions;
    }

    @Override
    public ObligationsStore obligations() {
        return obligations;
    }

    @Override
    public UserDefinedPMLStore userDefinedPML() {
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
