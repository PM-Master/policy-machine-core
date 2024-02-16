package gov.nist.csd.pm.pdp.neo4j;

import gov.nist.csd.pm.pdp.AccessReviewerTest;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.review.AccessReview;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;

public class Neo4jAccessReviewerTest extends AccessReviewerTest {
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
    public AccessReview getAccessReviewer() throws PMException {
        return new Neo4jAccessReviewer(embeddedDatabaseServer.defaultDatabaseService());
    }
}
