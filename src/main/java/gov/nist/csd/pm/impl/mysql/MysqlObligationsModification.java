package gov.nist.csd.pm.impl.mysql;

import com.google.gson.Gson;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.modification.ObligationsModification;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.common.obligation.Obligation;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.pap.exception.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class MysqlObligationsModification implements ObligationsModification {

    private MysqlConnection connection;

    public MysqlObligationsModification(MysqlConnection mysqlConnection) {
        this.connection = mysqlConnection;
    }

    @Override
    public void create(UserContext author, String name, Rule... rules)
            throws PMException {
        checkCreateInput(new MysqlGraphModification(connection), author, name, rules);

        String sql = """
                insert into obligation (name, author, rules) values (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, new Gson().toJson(author));
            ps.setBytes(3, serializeRules(rules));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public void update(UserContext author, String name, Rule... rules)
            throws PMException {
        checkUpdateInput(new MysqlGraphModification(connection), author, name, rules);

        connection.beginTx();

        try {
            delete(name);

            try {
                create(author, name, rules);
            } catch (ObligationNameExistsException e) {
                throw new PMBackendException(e);
            }

            connection.commit();
        } catch (MysqlPolicyException e) {
            connection.rollback();
            throw e;
        }
    }

    @Override
    public void delete(String name) throws MysqlPolicyException {
        String sql = """
                delete from obligation where name = ?
                """;

        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public List<Obligation> getAll() throws MysqlPolicyException {
        List<Obligation> obligations = new ArrayList<>();

        String sql = """
                select name, author, rules from obligation;
                """;

        try(Statement stmt = connection.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString(1);
                UserContext author = new Gson().fromJson(rs.getString(2), UserContext.class);
                Rule[] rules = deserializeRules(rs.getBlob(3).getBinaryStream().readAllBytes());

                obligations.add(new Obligation(author, name, List.of(rules)));
            }

            return obligations;
        } catch (SQLException | IOException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    @Override
    public boolean exists(String name) throws MysqlPolicyException {
        try {
            get(name);
            return true;
        } catch (ObligationDoesNotExistException e) {
            return false;
        }
    }

    @Override
    public Obligation get(String name) throws ObligationDoesNotExistException, MysqlPolicyException {
        String sql = """
                select author, rules from obligation where name = ?
                """;

        try(PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new ObligationDoesNotExistException(name);
            }

            String json = rs.getString(1);
            UserContext author = new Gson().fromJson(json, UserContext.class);
            Rule[] rules = deserializeRules(rs.getBlob(2).getBinaryStream().readAllBytes());

            return new Obligation(author, name, List.of(rules));
        } catch (SQLException | IOException e) {
            throw new MysqlPolicyException(e.getMessage());
        }
    }

    private static byte[] serializeRules(Rule[] rules) {
        return SerializationUtils.serialize(rules);
    }

    private static Rule[] deserializeRules(byte[] b) {
        return SerializationUtils.deserialize(b);
    }



}
