package gov.nist.csd.pm.pap.serialization.pml;

import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.admin.AdminPolicy;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.util.PolicyEquals;
import gov.nist.csd.pm.util.SamplePolicy;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

class PMLSerializerTest {

    String input = """
            set resource access rights ["read", "write", "execute"]
            
            create policy class "pc1"
            set properties of "pc1" to {"k":"v"}
            create oa "oa1" assign to ["pc1"]
            set properties of "oa1" to {"k1":"v1", "k2":"v2"}
            create ua "ua1" assign to ["pc1"]
            create u "u1" assign to ["ua1"]
            
            associate "ua1" and "oa1" with ["read", "write"]
            
            create o "o1" assign to ["oa1"]
            
            create prohibition "p1" deny user attribute "ua1" access rights ["read"] on union of [!"oa1"]
            create obligation "obl1" {
                create rule "rule1"
                when subject => pAny()
                performs op => pContainedIn(["event1", "event2"])
                do(evtCtx) {
                    event := evtCtx["event"]
                    if event == "event1" {
                        create policy class "e1"
                    } else if event == "event2" {
                        create policy class "e2"
                    }
                    
                    create prohibition "p1"
                    deny user attribute "ua1"
                    access rights ["read"]
                    on union of [!"oa1"]
                }
            }
            const testConst = "hello world"
            function testFunc() {
                create pc "pc1"
            }
            """;

    @Test
    void testSerialization() throws PMException {
        MemoryPAP pap = new MemoryPAP();
        UserContext userContext = new UserContext("u1");
        pap.deserialize(userContext, List.of(input), new PMLDeserializer());

        pap.modify().graph().createObjectAttribute("test-oa", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().assign(AdminPolicy.policyClassTargetName("pc1"), "test-oa");

        String expected = input + " create object attribute \"test-oa\" assign to [\"pc1\"]\n" + "assign \"pc1:target\" to [\"test-oa\"]";

        PAP testPAP = new MemoryPAP();
        testPAP.deserialize(userContext, List.of(expected), new PMLDeserializer());

        PolicyEquals.assertPolicyEquals(pap.query(), testPAP.query());
    }

    @Test
    void testSerialization2() throws PMException, IOException {
        MemoryPAP pap = new MemoryPAP();
        UserContext userContext = new UserContext("u1");

        SamplePolicy.loadSamplePolicyFromPML(pap);

        PAP testPAP = new MemoryPAP();
        String serialize = pap.serialize(new PMLSerializer());
        testPAP.deserialize(userContext, List.of(serialize), new PMLDeserializer());

        PolicyEquals.assertPolicyEquals(pap.query(), testPAP.query());
    }



}