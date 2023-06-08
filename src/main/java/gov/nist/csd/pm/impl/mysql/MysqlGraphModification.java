package gov.nist.csd.pm.impl.mysql;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.Graph;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.common.graph.nodes.Node;
import gov.nist.csd.pm.common.graph.nodes.NodeType;
import gov.nist.csd.pm.common.graph.nodes.Properties;
import gov.nist.csd.pm.common.graph.relationships.Association;
import gov.nist.csd.pm.pap.exception.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.common.graph.nodes.NodeType.*;
import static gov.nist.csd.pm.pap.AdminPolicyNode.POLICY_CLASS_TARGETS;

class MysqlGraph implements Graph {

    private MysqlConnection connection;

    public MysqlGraph(MysqlConnection connection) {
        this.connection = connection;
    }

    @Override
    public void setResourceAccessRights(AccessRightSet accessRightSet)
            throws PMException {
        checkSetResourceAccessRightsInput(accessRightSet);

        try {
            String sql = """
                insert into resource_access_rights (id, access_rights) values (1, ?) ON DUPLICATE KEY UPDATE access_rights = (?);
                """;
            try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
                String arJson = MysqlPolicyStore.arsetToJson(accessRightSet);
                ps.setString(1, arJson);
                ps.setString(2, arJson);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public AccessRightSet getResourceAccessRights() throws MysqlPolicyException {
        AccessRightSet arset = new AccessRightSet();
        String sql = """
                    select access_rights from resource_access_rights;
                    """;

        try(Statement stmt = connection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                arset = new Gson().fromJson(rs.getString(1), AccessRightSet.class);
            }

            return arset;
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public String createPolicyClass(String name, Map<String, String> properties)
            throws PMException {
        return createPolicyClassNode(name, properties);
    }

    @Override
    public String createUserAttribute(String name, Map<String, String> properties, List<String> parents)
            throws PMException {
        return createNode(name, UA, properties, parents);
    }

    @Override
    public String createObjectAttribute(String name, Map<String, String> properties, List<String> parents)
            throws PMException {
        return createNode(name, OA, properties, parents);
    }

    @Override
    public String createObject(String name, Map<String, String> properties, List<String> parents)
            throws PMException {
        return createNode(name, O, properties, parents);
    }

    @Override
    public String createUser(String name, Map<String, String> properties, List<String> parents)
            throws PMException {
        return createNode(name, U, properties, parents);
    }

    @Override
    public void setNodeProperties(String name, Map<String, String> properties)
            throws PMException {
        checkSetNodePropertiesInput(name);

        String sql = """
                    UPDATE node SET properties=? WHERE NAME=?
                    """;
        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, MysqlPolicyStore.toJSON(properties));
            ps.setString(2, name);
            ps.execute();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public boolean nodeExists(String name) throws MysqlPolicyException {
        String sql = """
                    SELECT count(*) FROM node WHERE name = ?
                    """;
        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return false;
            }

            int anInt = rs.getInt(1);
            boolean exists =  anInt == 1;

            rs.close();

            return exists;
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public Node getNode(String name) throws PMException {
        checkGetNodeInput(name);

        String sql = """
                    SELECT name, node_type_id, properties FROM node WHERE name = ?
                    """;
        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new NodeDoesNotExistException(name);
            }

            Node node = getNodeFromResultSet(rs);

            rs.close();

            return node;
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    protected static Node getNodeFromResultSet(ResultSet rs) throws MysqlPolicyException {
        try {
            String name = rs.getString(1);
            NodeType type = MysqlPolicyStore.getNodeTypeFromId(rs.getInt(2));

            Gson gson = new Gson();
            String json = rs.getString(3);

            Map<String, String> props = gson.fromJson(json, new TypeToken<Map<String, String>>() {}.getType());
            return new Node(name, type, props);
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public List<String> search(NodeType type, Map<String, String> properties) throws MysqlPolicyException {
        String sql = "select name from node";
        StringBuilder where = new StringBuilder();
        if (type != ANY) {
            where = new StringBuilder("node_type_id = " + MysqlPolicyStore.getNodeTypeId(type));
        }

        if (properties != null && !properties.isEmpty()) {
            for (String key : properties.keySet()) {
                if (where.length() > 0) {
                    where.append(" AND ");
                }

                String value = properties.get(key);

                where.append("properties -> '$.")
                        .append(key)
                        .append("' like '\"")
                        .append(value.equals(Properties.WILDCARD) ? "%%" : value)
                        .append("\"'");
            }
        }

        if (!where.isEmpty()) {
            sql = sql + " where " + where;
        }

        List<String> results = new ArrayList<>();
        try (Statement stmt = connection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }

        return results;
    }

    @Override
    public List<String> getPolicyClasses() throws MysqlPolicyException {
        List<String> policyClasses = new ArrayList<>();
        String sql = """
                    SELECT name FROM node WHERE node_type_id = ?
                    """;
        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, MysqlPolicyStore.getNodeTypeId(PC));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                policyClasses.add(rs.getString(1));
            }

            rs.close();

            return policyClasses;
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public void deleteNode(String name)
            throws PMException {
        if (!checkDeleteNodeInput(name, new MysqlProhibitions(connection), new MysqlObligations(connection))) {
            return;
        }

        Node node;
        try {
             node = getNode(name);
        } catch (NodeDoesNotExistException e) {
            // shouldn't get here but the node not existing should not throw an exception
            return;
        }

        String sql = """
                    DELETE FROM node WHERE NAME=?
                    """;
        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.execute();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }

        if (node.getType() == PC) {
            try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
                ps.setString(1, AdminPolicy.policyClassTargetName(name));
                ps.execute();
            } catch (SQLException e) {
                throw new MysqlPolicyException(e.getMessage());
            }
        }

    }


    @Override
    public void assign(String child, String parent)
            throws PMException {
        if (!checkAssignInput(child, parent)) {
            return;
        }

        String sql = """
            INSERT INTO assignment (start_node_id, end_node_id) VALUES (
              (SELECT id FROM node WHERE name=?), (SELECT id FROM node WHERE name=?)
            ) ON DUPLICATE KEY UPDATE start_node_id=start_node_id
            """;

        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, child);
            ps.setString(2, parent);
            ps.execute();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public void deassign(String child, String parent)
            throws PMException {
        if (!checkDeassignInput(child, parent)) {
            return;
        }

        String sql = """
            DELETE FROM assignment
            WHERE start_node_id = (SELECT id FROM node WHERE name=?)
            AND end_node_id = (SELECT id FROM node WHERE name=?)
            """;

        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, child);
            ps.setString(2, parent);
            ps.execute();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public List<String> getParents(String node) throws PMException {
        checkGetParentsInput(node);

        List<String> parents = new ArrayList<>();

        String sql = """
                    select parents.name from node
                    join assignment on node.id=assignment.start_node_id
                    join node as parents on parents.id=assignment.end_node_id
                    where node.name = ?;
                    """;

        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, node);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                parents.add(rs.getString(1));
            }

            rs.close();

            return parents;
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }


    @Override
    public List<String> getChildren(String node) throws PMException {
        checkGetChildrenInput(node);

        List<String> children = new ArrayList<>();

        String sql = """
                    select children.name from node
                    join assignment on node.id=assignment.end_node_id
                    join node as children on children.id=assignment.start_node_id
                    where node.name = ?;
                    """;

        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, node);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                children.add(rs.getString(1));
            }

            rs.close();

            return children;
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public void associate(String ua, String target, AccessRightSet accessRights)
            throws PMException {
        checkAssociateInput(ua, target, accessRights);

        String sql = """
            INSERT INTO association (start_node_id, end_node_id, operation_set) VALUES (
              (SELECT id FROM node WHERE name=?), (SELECT id FROM node WHERE name=?), ?
            ) ON DUPLICATE KEY UPDATE operation_set=?
            """;

        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ua);
            ps.setString(2, target);

            String json = MysqlPolicyStore.arsetToJson(accessRights);
            ps.setString(3, json);
            ps.setString(4, json);
            ps.execute();
        }catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public void dissociate(String ua, String target) throws PMException {
        if (!checkDissociateInput(ua, target)) {
            return;
        }

        String sql = """
            DELETE FROM association
            WHERE start_node_id = (SELECT id FROM node WHERE name=?)
            AND end_node_id = (SELECT id FROM node WHERE name=?)
            """;

        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ua);
            ps.setString(2, target);
            ps.execute();
        }catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public List<Association> getAssociationsWithSource(String ua) throws PMException {
        checkGetAssociationsWithSourceInput(ua);

        List<Association> associations = new ArrayList<>();

        String sql = """
                    select targets.name, association.operation_set from node
                    join association on node.id=association.start_node_id
                    join node as targets on targets.id=association.end_node_id
                    where node.name = ?;
                    """;
        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ua);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String target = rs.getString(1);
                AccessRightSet arset = new Gson().fromJson(rs.getString(2), AccessRightSet.class);
                associations.add(new Association(ua, target, arset));
            }

            rs.close();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }

        return associations;
    }

    @Override
    public List<Association> getAssociationsWithTarget(String target)
            throws PMException {
        checkGetAssociationsWithTargetInput(target);

        List<Association> associations = new ArrayList<>();

        String sql = """
                    select sources.name, association.operation_set from node
                    join association on node.id=association.end_node_id
                    join node as sources on sources.id=association.start_node_id
                    where node.name = ?;
                    """;
        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, target);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String source = rs.getString(1);
                AccessRightSet arset = new Gson().fromJson(rs.getString(2), AccessRightSet.class);
                associations.add(new Association(source, target, arset));
            }

            rs.close();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }

        return associations;
    }

    private String createPolicyClassNode(String name, Map<String, String> properties)
            throws PMException {
        checkCreatePolicyClassInput(name);

        connection.beginTx();

        createNodeInternal(name, PC, properties);

        // create pc rep oa or verify that its assigned to the POLICY_CLASS_TARGETS node if already created
        String pcTarget = AdminPolicy.policyClassTargetName(name);
        if (!nodeExists(pcTarget)) {
            createNodeInternal(pcTarget, OA, new HashMap<>());
        }

        try {
            if (!getParents(pcTarget).contains(POLICY_CLASS_TARGETS.nodeName())) {
                assignInternal(pcTarget, POLICY_CLASS_TARGETS.nodeName());
            }
        } catch (NodeDoesNotExistException e) {
            throw new PMBackendException("error creating target attribute for policy class " + name, e);
        }

        connection.commit();

        return name;
    }

    @Override
    public void checkAssignmentDoesNotCreateLoop(String child, String parent)
            throws PMException {
        // TODO embed detection in mysql for better performance than using default impl
        Graph.super.checkAssignmentDoesNotCreateLoop(child, parent);
    }

    private String createNode(String name, NodeType type, Map<String, String> properties, List<String> parents)
            throws PMException {
        checkCreateNodeInput(name, type, parents);

        connection.beginTx();

        // create the node in the node table
        createNodeInternal(name, type, properties);

        // assign the node to any additional parents
        for (String p : parents) {
            assignInternal(name, p);
        }

        connection.commit();

        return name;
    }

    protected void createNodeInternal(String name, NodeType type, Map<String, String> properties) throws MysqlPolicyException {
        if (properties == null) {
            properties = new HashMap<>();
        }

        String sql = """
                    INSERT INTO node (node_type_id, name, properties) VALUES (?,?,?)
                    """;
        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            int nodeTypeIdIndex = 1;
            int nameIndex = 2;
            int propertiesIndex = 3;
            ps.setInt(nodeTypeIdIndex, MysqlPolicyStore.getNodeTypeId(type));
            ps.setString(nameIndex, name);
            ps.setString(propertiesIndex, MysqlPolicyStore.toJSON(properties));
            ps.execute();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    protected void assignInternal(String child, String parent) throws MysqlPolicyException {
        String sql = """
            INSERT INTO assignment (start_node_id, end_node_id) VALUES (
              (SELECT id FROM node WHERE name=?), (SELECT id FROM node WHERE name=?)
            ) ON DUPLICATE KEY UPDATE start_node_id=start_node_id
            """;

        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, child);
            ps.setString(2, parent);
            ps.execute();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }
}
