package gov.nist.csd.pm.pap.serialization.json;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
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

        PAP pap = new MemoryPAP();
        pap.deserialize(new UserContext("u1"), json, new JSONDeserializer());

        String serialize = pap.serialize(new JSONSerializer());

        PAP pap2 = new MemoryPAP();
        pap2.deserialize(new UserContext("u1"), serialize, new JSONDeserializer());

        PolicyEquals.assertPolicyEquals(pap.query(), pap2.query());
    }
}
