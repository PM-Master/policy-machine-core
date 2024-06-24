package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.tx.Transactional;
import gov.nist.csd.pm.pap.query.PolicyQuerier;

public abstract class Modifier implements Transactional {

    public abstract PolicyQuerier query();

    protected <T> T runTx(Runner<T> txRunner) throws PMException {
        try {
            beginTx();
            T result = txRunner.run();
            commit();
            return result;
        } catch (PMException e) {
            rollback();
            throw e;
        }
    }

    protected void runTx(VoidRunner txRunner) throws PMException {
        try {
            beginTx();
            txRunner.run();
            commit();
        } catch (PMException e) {
            rollback();
            throw e;
        }
    }

    public interface Runner<T> {
        T run() throws PMException;
    }

    public interface VoidRunner {
        void run() throws PMException;
    }
}
