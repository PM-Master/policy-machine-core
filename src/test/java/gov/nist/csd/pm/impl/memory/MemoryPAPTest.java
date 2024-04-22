package gov.nist.csd.pm.impl.memory;

import com.google.gson.Gson;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PAPTest;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.graph.nodes.Node;
import gov.nist.csd.pm.common.graph.nodes.Properties;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MemoryPAPTest extends PAPTest{

    @Override
    public PAP getPAP() throws PMException {
        return new PAP(new MemoryPolicyStore());
    }

    @Test
    void testJsonAsPropertyValue() throws PMException {
        PAP pap = new PAP(new MemoryPolicyStore());
        pap.graph().createPolicyClass("name", Properties.toProperties("test", "{\"12\": \"34\"}", "a", "[\"1\", \"2\"]"));
        Node name = pap.graph().getNode("name");
        Map<String, String> properties = name.getProperties();
        String test = properties.get("test");
        Map m = new Gson().fromJson(test, Map.class);
        assertEquals("34", m.get("12"));

        String json = String.valueOf(properties.get("a"));
        assertArrayEquals(new String[]{"1", "2"}, new Gson().fromJson(json, String[].class));
    }

}