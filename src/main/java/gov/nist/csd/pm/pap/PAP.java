package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.pap.serialization.PolicyDeserializer;
import gov.nist.csd.pm.pap.serialization.PolicySerializer;
import gov.nist.csd.pm.pap.modification.PolicyModification;
import gov.nist.csd.pm.pap.pml.PMLExecutable;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.PolicyQuery;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.common.tx.Transactional;

public abstract class PAP implements Transactional, PMLExecutable {

    public abstract PolicyModification modify();

    public abstract PolicyQuery query();

    public abstract void reset() throws PMException;

    @Override
    public void executePML(UserContext userContext, String input, FunctionDefinitionStatement... functionDefinitionStatements) throws PMException {
        PMLExecutor.compileAndExecutePML(this, userContext, input, functionDefinitionStatements);
    }

    @Override
    public void executePMLFunction(UserContext userContext, String functionName, Value... args) throws PMException {
        String pml = String.format("%s(%s)", functionName, PMLExecutable.valuesToArgs(args));

        // execute function as pml
        PMLExecutor.compileAndExecutePML(this, userContext, pml);
    }

    /**
     * Serialize the current policy state with the given PolicySerializer.]
     *
     * @param serializer The PolicySerializer used to generate the output String.
     * @return The string representation of the policy.
     * @throws PMException If there is an error during the serialization process.
     */
    public String serialize(PolicySerializer serializer) throws PMException {
        return serializer.serialize(query());
    }

    /**
     * Deserialize the given input string into the current policy state. The user defined in the UserContext needs to exist
     * in the graph created if any obligations are created. If the user does not exist before an obligation is created
     * an exception will be thrown. This method also resets the policy before deserialization. However, the {@link AdminPolicy}
     * nodes are assumed to be created and can be referenced in the input string without explicit creation. If any of the
     * admin policy nodes are created in the input string an exception will be thrown.
     *
     * @param author The UserContext describing the author of the deserialized policy elements.
     * @param input The string representation of the policy to deserialize.
     * @param policyDeserializer The PolicyDeserializer to apply the input string to the policy.
     * @throws PMException If there is an error deserializing the given inputs string.
     */
    public void deserialize(UserContext author, String input, PolicyDeserializer policyDeserializer) throws PMException {
        beginTx();
        reset();

        try {
            policyDeserializer.deserialize(this, author, input);
        } catch (PMException e) {
            rollback();
            throw e;
        }

        commit();
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
