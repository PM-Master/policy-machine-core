package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.pap.pml.PMLExecutable;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.PolicyQuery;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.common.tx.Transactional;

public class PAP implements Transactional, PMLExecutable {

    protected final PolicyModifier modifier;
    protected final PolicyQuery querier;

    public PAP(PolicyModifier modifier, PolicyQuery querier) throws PMException {
        this.modifier = modifier;
        this.querier = querier;
    }

    public PolicyModifier policy() {
        return modifier;
    }

    public PolicyQuery review() {
        return querier;
    }

    @Override
    public void beginTx() throws PMException {
        modifier.beginTx();
    }

    @Override
    public void commit() throws PMException {
        modifier.commit();
    }

    @Override
    public void rollback() throws PMException {
        modifier.rollback();
    }

    @Override
    public void executePML(UserContext userContext, String input, FunctionDefinitionStatement... functionDefinitionStatements) throws PMException {
        PMLExecutor.compileAndExecutePML(this.modifier, userContext, input, functionDefinitionStatements);
    }

    @Override
    public void executePMLFunction(UserContext userContext, String functionName, Value... args) throws PMException {
        String pml = String.format("%s(%s)", functionName, PMLExecutable.valuesToArgs(args));

        // execute function as pml
        PMLExecutor.compileAndExecutePML(this.modifier, userContext, pml);
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
