package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.pap.pml.PMLExecutable;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.common.tx.Transactional;
import gov.nist.csd.pm.common.serialization.PolicyDeserializer;
import gov.nist.csd.pm.common.serialization.PolicySerializer;

public class PAP implements Transactional, PMLExecutable {

    protected final PolicyStore policyStore;
    protected final PolicyReview policyReview;

    public PAP(PolicyStore policyStore, PolicyReview policyReview) throws PMException {
        this.policyStore = policyStore;
        this.policyReview = policyReview;
    }

    public PolicyStore policy() {
        return policyStore;
    }

    public PolicyReview review() {
        return policyReview;
    }

    @Override
    public void beginTx() throws PMException {
        policyStore.beginTx();
    }

    @Override
    public void commit() throws PMException {
        policyStore.commit();
    }

    @Override
    public void rollback() throws PMException {
        policyStore.rollback();
    }

    @Override
    public void executePML(UserContext userContext, String input, FunctionDefinitionStatement... functionDefinitionStatements) throws PMException {
        PMLExecutor.compileAndExecutePML(this.policyStore, userContext, input, functionDefinitionStatements);
    }

    @Override
    public void executePMLFunction(UserContext userContext, String functionName, Value... args) throws PMException {
        String pml = String.format("%s(%s)", functionName, PMLExecutable.valuesToArgs(args));

        // execute function as pml
        PMLExecutor.compileAndExecutePML(this.policyStore, userContext, pml);
    }

    public void runTx(TxRunner txRunner) throws PMException {
        beginTx();

        try {
            txRunner.runTx(this);

            commit();
        } catch (PMException e) {
            rollback();
            throw e;
        }
    }

    public interface TxRunner {
        void runTx(PAP pap) throws PMException;
    }
}
