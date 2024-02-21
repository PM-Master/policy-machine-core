package gov.nist.csd.pm.impl.neo4j;

import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.tx.Transactional;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

public class Neo4jConnection implements Transactional {

    private GraphDatabaseService graph;
    private Transaction tx;
    private int txCounter;

    public Neo4jConnection(GraphDatabaseService graph) {
        this.graph = graph;
    }

    @Override
    public void beginTx() {
        txCounter++;

        if (tx != null) {
            return;
        }

        tx = graph.beginTx();
    }

    @Override
    public void commit() {
        if (txCounter > 1) {
            txCounter--;
            return;
        } else if (txCounter == 0) {
            return;
        }

        tx.commit();
        tx.close();
        tx = null;
        txCounter = 0;
    }

    @Override
    public void rollback() {
        if (tx == null) {
            return;
        }

        tx.rollback();
        tx.close();
        tx = null;
        txCounter = 0;
    }
    
    public void runTx(VoidTxRunner runner) throws PMException {
        beginTx();

        try {
            runner.runTx(tx);

            commit();
        } catch (PMException e) {
            rollback();

            throw e;
        }
    }

    public <T> T runTx(TxRunner<T> runner) throws PMException {
        beginTx();

        try {
            T t = runner.runTx(tx);

            commit();

            return t;
        } catch (PMException e) {
            rollback();

            throw e;
        }
    }

    public interface TxRunner<T> {
        T runTx(Transaction tx) throws PMException;
    }

    public interface VoidTxRunner {
        void runTx(Transaction tx) throws PMException;
    }
}
