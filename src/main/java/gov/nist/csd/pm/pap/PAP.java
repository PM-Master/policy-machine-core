package gov.nist.csd.pm.pap;

import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.modification.PolicyModifier;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.pml.CompiledPML;
import gov.nist.csd.pm.pap.pml.PMLCompiler;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.ExecuteGlobalScope;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;
import gov.nist.csd.pm.pap.query.PolicyQuerier;
import gov.nist.csd.pm.pap.serialization.PolicyDeserializer;
import gov.nist.csd.pm.pap.serialization.PolicySerializer;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Collection;
import java.util.List;

public abstract class PAP implements PolicyPoint {

    public abstract PolicyModifier modify();

    public abstract PolicyQuerier query();

    public PAP withTransientAdminOps(Collection<Operation> ops) {
        query().setTransientAdminOperations(ops);
        return this;
    }

    @Override
    public void executePML(UserContext author, String input) throws PMException {
        PMLCompiler pmlCompiler = new PMLCompiler()
                .withFunctions(query().operations().getAdminOperations());
        CompiledPML compiledPML = pmlCompiler.compilePML(input);
        List<PMLStatementSerializer> stmts = compiledPML.stmts();

        // add the constants and functions to the persisted scope
        // build a global scope from the policy
        GlobalScope<Value> globalScope = new ExecuteGlobalScope()
                .withFunctions(pmlCompiler.getFunctions());

        // execute other statements
        ExecutionContext ctx = new ExecutionContext(author, new Scope<>(globalScope));

        for (PMLStatementSerializer stmt : stmts) {
            ctx.getExecutor().executeStatement(ctx, this, stmt);
        }
    }

    /**
     * Serialize the current policy state with the given PolicySerializer.
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
    public void deserialize(UserContext author, Collection<String> input, PolicyDeserializer policyDeserializer) throws PMException {
        beginTx();
        reset();

        try {
            for (String i : input) {
                policyDeserializer.deserialize(this, author, i);
            }
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
