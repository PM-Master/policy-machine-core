package gov.nist.csd.pm.impl.mysql;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PAPTest;
import gov.nist.csd.pm.common.exception.PMException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MysqlPAPTest extends PAPTest {

    static MysqlTestEnv testEnv;

    @BeforeAll
    static void start() throws PMException, IOException {
        testEnv = new MysqlTestEnv();
        testEnv.start();
    }

    @AfterAll
    static void stop() {
        testEnv.stop();
    }

    @AfterEach
    void reset() throws SQLException {
        connection.close();
        testEnv.reset();
    }

    private Connection connection;

    @Override
    public PAP getPAP() throws PMException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new PMException(e);
            }
        }

        try {
            connection = DriverManager.getConnection(testEnv.getConnectionUrl(), testEnv.getUser(), testEnv.getPassword());
        } catch (SQLException e) {
            throw new PMException(e);
        }

        MysqlPolicyStore ps = new MysqlPolicyStore(connection);
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        return new PAP(ps, pr);
    }

    @Test
    void testTx() throws SQLException, PMException {
        try (Connection connection = DriverManager.getConnection(testEnv.getConnectionUrl(), testEnv.getUser(), testEnv.getPassword());
             Connection connection2 = DriverManager.getConnection(testEnv.getConnectionUrl(), testEnv.getUser(), testEnv.getPassword())) {

            MysqlPolicyStore ps = new MysqlPolicyStore(connection);
            MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
            PAP pap = new PAP(ps, pr);
            pap.beginTx();
            pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
            pap.policy().graph().createObjectAttribute("oa1", new HashMap<>(), List.of("pc1"));
            pap.commit();

            MysqlPolicyStore ps2 = new MysqlPolicyStore(connection2);
            PAP pap2 = new PAP(ps2, new MemoryPolicyReviewer(ps2));
            assertTrue(pap2.policy().graph().nodeExists("pc1"));
            assertTrue(pap2.policy().graph().nodeExists("oa1"));
        }
    }
}