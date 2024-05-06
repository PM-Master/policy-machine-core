package gov.nist.csd.pm.pap.serialization;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.serialization.json.JSONDeserializer;
import gov.nist.csd.pm.common.serialization.json.JSONSerializer;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.util.PolicyEquals;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONTest {

    @Test
    void testSerialization() throws PMException, IOException {
        String json = IOUtils.resourceToString("json/JSONTest.json", StandardCharsets.UTF_8, JSONTest.class.getClassLoader());

        MemoryPolicyModifier ps = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        pap.policy().deserialize(new UserContext("u1"), json, new JSONDeserializer());

        String serialize = pap.policy().serialize(new JSONSerializer());

        assertEquals(json, serialize);

        MemoryPolicyModifier ps2 = new MemoryPolicyModifier();
        MemoryPolicyReviewer pr2 = new MemoryPolicyReviewer(ps2);
        PAP pap2 = new PAP(ps2, pr2);
        pap2.policy().deserialize(new UserContext("u1"), serialize, new JSONDeserializer());

        PolicyEquals.assertPolicyEquals(pap.policy(), pap2.policy());
    }
}
