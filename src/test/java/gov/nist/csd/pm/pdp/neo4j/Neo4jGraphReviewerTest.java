package gov.nist.csd.pm.pdp.neo4j;

import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.neo4j.Neo4jPolicyStore;
import gov.nist.csd.pm.pdp.GraphReviewerTest;
import gov.nist.csd.pm.policy.exceptions.PMException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

public class Neo4jGraphReviewerTest extends GraphReviewerTest {

    private static Neo4j embeddedDatabaseServer;

    @BeforeAll
    static void initializeNeo4j() {
        embeddedDatabaseServer = Neo4jBuilders.newInProcessBuilder()
                                              .withDisabledServer()
                                              .build();
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

    @Override
    public TestContext initTest() throws PMException {
        PAP pap = new PAP(new Neo4jPolicyStore(embeddedDatabaseServer.defaultDatabaseService()));
        return new TestContext(new Neo4jGraphReviewer(embeddedDatabaseServer.defaultDatabaseService()), pap);
    }
}
