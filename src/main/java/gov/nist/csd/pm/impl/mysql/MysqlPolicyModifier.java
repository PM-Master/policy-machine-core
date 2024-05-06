package gov.nist.csd.pm.impl.mysql;

import com.google.gson.Gson;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PolicyModifier;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.PMLConstantAlreadyDefinedException;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.pap.pml.value.StringValue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.*;
import static gov.nist.csd.pm.pap.AdminPolicy.Verifier;
import static gov.nist.csd.pm.pap.AdminPolicy.verify;

public class MysqlPolicyModifier extends PolicyModifier implements Verifier {

    protected final MysqlConnection connection;

    private final MysqlGraphModification graph;
    private final MysqlProhibitionsModification prohibitions;
    private final MysqlObligationsModification obligations;
    private final MysqlPMLModification userDefinedPML;

    public MysqlPolicyModifier(Connection connection) throws PMException {
        this.connection = new MysqlConnection(connection);

        this.graph = new MysqlGraphModification(this.connection);
        this.prohibitions = new MysqlProhibitionsModification(this.connection);
        this.obligations = new MysqlObligationsModification(this.connection);
        this.userDefinedPML = new MysqlPMLModification(this.connection);

        verify(this, graph);
    }

    @Override
    public void beginTx() throws MysqlPolicyException {
        connection.beginTx();
    }

    @Override
    public void commit() throws MysqlPolicyException {
        connection.commit();
    }

    @Override
    public void rollback() throws MysqlPolicyException {
        connection.rollback();
    }

    @Override
    public void reset() throws PMException {
        beginTx();

        List<String> sequence = PolicyResetSequence.getSequence();
        try (Statement stmt = connection.getConnection().createStatement()) {
            for (String s : sequence) {
                stmt.executeUpdate(s);
            }
        } catch (SQLException e) {
            rollback();
            throw new MysqlPolicyException(e.getMessage());
        }

        verify(this, graph);

        commit();
    }

    @Override
    public MysqlGraphModification graph() {
        return graph;
    }

    @Override
    public MysqlProhibitionsModification prohibitions() {
        return prohibitions;
    }

    @Override
    public MysqlObligationsModification obligations() {
        return obligations;
    }

    @Override
    public MysqlPMLModification pml() {
        return userDefinedPML;
    }

    static int getNodeTypeId(NodeType nodeType) {
        // values are mapped to values in node_type table
        return switch (nodeType) {
            case PC -> 5;
            case OA -> 1;
            case UA -> 2;
            case O -> 4;
            default -> 3; // U
        };
    }

    static NodeType getNodeTypeFromId(int id) {
        // values are mapped to values in node_type table
        return switch (id) {
            case 1 -> OA;
            case 2 -> UA;
            case 3 -> U;
            case 4 -> O;
            default -> PC;
        };
    }

    public static String toJSON(Map<String, String> map) {
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    public static String arsetToJson(AccessRightSet set) {
        Gson gson = new Gson();
        return gson.toJson(set);
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
