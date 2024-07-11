package gov.nist.csd.pm.pap.serialization;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.serialization.json.JSONDeserializer;
import gov.nist.csd.pm.pap.serialization.json.JSONSerializer;
import gov.nist.csd.pm.pap.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.pap.serialization.pml.PMLSerializer;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.util.PolicyEquals;
import gov.nist.csd.pm.util.SamplePolicy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.util.PolicyEquals.assertPolicyEquals;

public class SerializationTest {

    /*
    TODO test failing because ua1 associations are not being serialized properly
    @Test
    void testJSONAndPML() throws PMException, IOException {
        MemoryPAP pap = new MemoryPAP();
        SamplePolicy.loadSamplePolicyFromPML(pap);

        String json = pap.serialize(new JSONSerializer());
        String pml = pap.serialize(new PMLSerializer());

        MemoryPAP jsonPAP = new MemoryPAP();
        jsonPAP.deserialize(new UserContext("u1"), json, new JSONDeserializer());

        PAP pmlPAP = new MemoryPAP();
        pmlPAP.deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        assertPolicyEquals(jsonPAP.query(), pmlPAP.query());
        assertPolicyEquals(pap.query(), pmlPAP.query());
        assertPolicyEquals(pap.query(), jsonPAP.query());
    }*/

    @Test
    void testPolicyClassTargets() throws PMException {
        MemoryPAP pap = new MemoryPAP();

        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createPolicyClass("pc2", new HashMap<>());
        pap.modify().graph().assign(AdminPolicy.policyClassTargetName("pc1"), "pc2");
        pap.modify().graph().assign(AdminPolicy.policyClassTargetName("pc2"), "pc1");

        String json = pap.serialize(new JSONSerializer());
        String pml = pap.serialize(new PMLSerializer());

        PAP jsonPAP = new MemoryPAP();
        jsonPAP.deserialize(new UserContext("u1"), List.of(json), new JSONDeserializer());

        PAP pmlPAP = new MemoryPAP();
        pmlPAP.deserialize(new UserContext("u1"), List.of(pml), new PMLDeserializer());

        assertPolicyEquals(jsonPAP.query(), pmlPAP.query());
        assertPolicyEquals(pap.query(), pmlPAP.query());
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
        MemoryPAP pap = new MemoryPAP();
        PMLExecutor.compileAndExecutePML(pap, new UserContext("u1"), pml);

        pml = pap.serialize(new PMLSerializer());
        String json = pap.serialize(new JSONSerializer());

        PAP jsonPAP = new MemoryPAP();
        jsonPAP.deserialize(new UserContext("u1"), List.of(json), new JSONDeserializer());

        PAP pmlPAP = new MemoryPAP();
        pmlPAP.deserialize(new UserContext("u1"), List.of(pml), new PMLDeserializer());

        assertPolicyEquals(jsonPAP.query(), pmlPAP.query());
        assertPolicyEquals(pap.query(), pmlPAP.query());
        assertPolicyEquals(pap.query(), pmlPAP.query());
    }

    @Test
    void testPMLAndJson() throws PMException, IOException {
        MemoryPAP pml = new MemoryPAP();
        MemoryPAP json = new MemoryPAP();

        SamplePolicy.loadSamplePolicyFromPML(pml);
        SamplePolicy.loadSamplePolicyFromJSON(json);

        PolicyEquals.assertPolicyEquals(pml.query(), json.query());

        String pmlStr = pml.serialize(new PMLSerializer());
        String jsonStr = json.serialize(new JSONSerializer());

        pml.deserialize(new UserContext("u1"), List.of(pmlStr), new PMLDeserializer());
        json.deserialize(new UserContext("u1"), List.of(pmlStr), new PMLDeserializer());
        PolicyEquals.assertPolicyEquals(pml.query(), json.query());

        pml.deserialize(new UserContext("u1"), List.of(jsonStr), new JSONDeserializer());
        json.deserialize(new UserContext("u1"), List.of(jsonStr), new JSONDeserializer());
        PolicyEquals.assertPolicyEquals(pml.query(), json.query());
    }
}
