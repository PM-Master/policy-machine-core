package gov.nist.csd.pm.pap.serialization;

import gov.nist.csd.pm.common.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.common.serialization.pml.PMLSerializer;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.util.PolicyEquals;
import gov.nist.csd.pm.util.SamplePolicy;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PMLTest {

    @Test
    void testDeserialization() throws IOException, PMException {
        String pml = SamplePolicy.loadSamplePolicyPML();

        PMLDeserializer pmlDeserializer = new PMLDeserializer();

        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        pap.policy().deserialize(new UserContext("u1"), pml, pmlDeserializer);

        String serialize = pap.policy().serialize(new PMLSerializer());
        MemoryPolicyModifier ps2 = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr2 = new MemoryPolicyReviewer(ps2);
        PAP pap2 = new PAP(ps2, pr2);
        pap2.policy().deserialize(new UserContext("u1"), serialize, pmlDeserializer);

        PolicyEquals.assertPolicyEquals(pap.policy(), pap2.policy());
    }

    @Test
    void testDeserializationWithCustomFunctions() throws IOException, PMException {
        String pml = """
               testFunc("hello world")
               """;

        PMLDeserializer pmlDeserializer = new PMLDeserializer();

        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        assertThrows(PMException.class, () -> pap.policy().deserialize(new UserContext("u1"), pml, pmlDeserializer));

        FunctionDefinitionStatement testFunc = new FunctionDefinitionStatement.Builder("testFunc")
                .returns(Type.voidType())
                .args(
                        new FormalArgument("name", Type.string())
                )
                .executor((ctx, policy) -> {
                    policy.graph().createPolicyClass(ctx.scope().getVariable("name").getStringValue(), new HashMap<>());

                    return new VoidValue();
                })
                .build();

        PMLDeserializer pmlDeserializer2 = new PMLDeserializer(testFunc);
        pap.policy().deserialize(new UserContext("u1"), pml, pmlDeserializer2);
        assertTrue(pap.policy().graph().nodeExists("hello world"));
    }

}