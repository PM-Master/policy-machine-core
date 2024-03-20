package gov.nist.csd.pm.pap.neo4j;

import gov.nist.csd.pm.impl.neo4j.pap.Neo4jPolicyStore;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PAPTest;
import gov.nist.csd.pm.policy.exceptions.PMException;
import org.junit.jupiter.api.*;
import org.neo4j.graphdb.*;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

public class Neo4jPAPTest extends PAPTest {


    private static Neo4j embeddedDatabaseServer;

    @BeforeAll
    static void initializeNeo4j() {
        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                                              .withDisabledServer()
                                              .build();
    }

    @Override
    public PAP getPAP() throws PMException {
        return new PAP(new Neo4jPolicyStore(embeddedDatabaseServer.defaultDatabaseService()));
    }

    @AfterEach
    void cleanupData() {
        GraphDatabaseService serv = embeddedDatabaseServer.defaultDatabaseService();
        try(Transaction tx = serv.beginTx();) {
            tx.execute("match (n) detach delete n");
            tx.commit();
        }
    }

    @AfterAll
    static void close(){
        embeddedDatabaseServer.close();
    }
}
