package gov.nist.csd.pm.pap.serialization;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.AdminPolicy;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.common.serialization.json.JSONDeserializer;
import gov.nist.csd.pm.common.serialization.json.JSONSerializer;
import gov.nist.csd.pm.common.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.common.serialization.pml.PMLSerializer;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.util.SamplePolicy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

import static gov.nist.csd.pm.util.PolicyEquals.assertPolicyEquals;

public class SerializationTest {

    @Test
    void testJSONAndPML() throws PMException, IOException {
        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        SamplePolicy.loadSamplePolicyFromPML(pap);

        String json = pap.policy().serialize(new JSONSerializer());
        String pml = pap.policy().serialize(new PMLSerializer());

        MemoryPolicyModifier jsonPS = new MemoryPolicyModifier();
        MemoryPolicyReviewer jsonPR = new MemoryPolicyReviewer(jsonPS);
        PAP jsonPAP = new PAP(jsonPS, jsonPR);
        jsonPAP.policy().deserialize(new UserContext("u1"), json, new JSONDeserializer());

        MemoryPolicyModifier pmlPS = new MemoryPolicyModifier();
        MemoryPolicyReviewer pmlPR = new MemoryPolicyReviewer(pmlPS);
        PAP pmlPAP = new PAP(pmlPS, pmlPR);
        pmlPAP.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        assertPolicyEquals(jsonPAP.policy(), pmlPAP.policy());
        assertPolicyEquals(pap.policy(), pmlPAP.policy());
        assertPolicyEquals(pap.policy(), pmlPAP.policy());
    }

    @Test
    void testPolicyClassTargets() throws PMException {
        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);

        pap.policy().graph().createPolicyClass("pc1", new HashMap<>());
        pap.policy().graph().createPolicyClass("pc2", new HashMap<>());
        pap.policy().graph().assign(AdminPolicy.policyClassTargetName("pc1"), "pc2");
        pap.policy().graph().assign(AdminPolicy.policyClassTargetName("pc2"), "pc1");

        String json = pap.policy().serialize(new JSONSerializer());
        String pml = pap.policy().serialize(new PMLSerializer());

        MemoryPolicyModifier jsonPS = new MemoryPolicyModifier();
        MemoryPolicyReviewer jsonPR = new MemoryPolicyReviewer(jsonPS);
        PAP jsonPAP = new PAP(jsonPS, jsonPR);
        jsonPAP.policy().deserialize(new UserContext("u1"), json, new JSONDeserializer());

        MemoryPolicyModifier pmlPS = new MemoryPolicyModifier();
        MemoryPolicyReviewer pmlPR = new MemoryPolicyReviewer(pmlPS);
        PAP pmlPAP = new PAP(pmlPS, pmlPR);
        pmlPAP.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        assertPolicyEquals(jsonPAP.policy(), pmlPAP.policy());
        assertPolicyEquals(pap.policy(), pmlPAP.policy());
        assertPolicyEquals(pap.policy(), pmlPAP.policy());
    }

    @Test
    void testJSONAndPMLWithFunctionsAndConstants() throws PMException {
        String pml = """
                function f1() {
                    f2()
                }
                
                function f2() {
                    create pc a
                }
                
                const a = "a"
                """;
        MemoryPolicyModifier memoryPolicyStore = new MemoryPolicyModifier();
        PMLExecutor.compileAndExecutePML(memoryPolicyStore, new UserContext("u1"), pml);

        pml = memoryPolicyStore.serialize(new PMLSerializer());
        String json = memoryPolicyStore.serialize(new JSONSerializer());

        MemoryPolicyModifier jsonPS = new MemoryPolicyModifier();
        MemoryPolicyReviewer jsonPR = new MemoryPolicyReviewer(jsonPS);
        PAP jsonPAP = new PAP(jsonPS, jsonPR);
        jsonPAP.policy().deserialize(new UserContext("u1"), json, new JSONDeserializer());

        MemoryPolicyModifier pmlPS = new MemoryPolicyModifier();
        MemoryPolicyReviewer pmlPR = new MemoryPolicyReviewer(pmlPS);
        PAP pmlPAP = new PAP(pmlPS, pmlPR);
        pmlPAP.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        assertPolicyEquals(jsonPAP.policy(), pmlPAP.policy());
        assertPolicyEquals(memoryPolicyStore, pmlPAP.policy());
        assertPolicyEquals(memoryPolicyStore, pmlPAP.policy());
    }

}
