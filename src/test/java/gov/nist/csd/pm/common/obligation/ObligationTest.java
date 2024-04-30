package gov.nist.csd.pm.common.obligation;

import gov.nist.csd.pm.epp.EPP;
import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.common.op.graph.AssignToOp;
import gov.nist.csd.pm.pap.pml.expression.reference.ReferenceByID;
import gov.nist.csd.pm.pap.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.common.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.pdp.PDP;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObligationTest {

    @Test
    void testResponseExecutionWithUserDefinedAndBuiltinPML() throws PMException {
        String pml = """
                create pc "pc1"
                create oa "oa1" assign to ["pc1"]
                create ua "ua1" assign to ["pc1"]
                create u "u1" assign to ["ua1"]
                
                associate "ua1" and POLICY_CLASS_TARGETS with [create_policy_class]
                const x = "hello world"
                function createX() {
                    create policy class x
                }
                
                create obligation "obl1" {
                    create rule "rule1"
                    when any user
                    performs ["assign_to"]
                    on ["oa1"]
                    do(ctx) {
                        createX()
                    }
                }
                """;
        MemoryPolicyStore policyStore = new MemoryPolicyStore();
        MemoryPolicyReviewer reviewer = new MemoryPolicyReviewer(policyStore);
        PAP pap = new PAP(policyStore, reviewer);
        pap.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());
        PDP pdp = new PDP(pap);
        EPP epp = new EPP(pdp, pap);
        epp.getEventProcessor().processEvent(new EventContext(new UserContext("u1"), new AssignToOp("o1", "oa1")));
        assertTrue(pap.policy().graph().nodeExists("hello world"));
    }

    @Test
    void testResponseWithExistingFunction() throws PMException {
        String pml = """
                create pc "pc1"
                create oa "oa1" assign to ["pc1"]
                create ua "ua1" assign to ["pc1"]
                create u "u1" assign to ["ua1"]
                
                associate "ua1" and POLICY_CLASS_TARGETS with [create_policy_class]
                
                create obligation "obl1" {
                    create rule "rule1"
                    when any user
                    performs ["assign_to"]
                    on ["oa1"]
                    do(ctx) {
                        createX()
                    }
                }
                """;

        MemoryPolicyStore policyStore = new MemoryPolicyStore();
        MemoryPolicyReviewer reviewer = new MemoryPolicyReviewer(policyStore);
        PAP pap = new PAP(policyStore, reviewer);

        pap.policy().userDefinedPML().createConstant("x", new StringValue("hello world"));
        pap.policy().userDefinedPML().createFunction(new FunctionDefinitionStatement.Builder("createX")
                .body(List.of(new CreatePolicyStatement(new ReferenceByID("x"))))
                .build());
        pap.executePML(new UserContext("u1"), pml);

        PDP pdp = new PDP(pap);
        EPP epp = new EPP(pdp, pap);
        epp.getEventProcessor().processEvent(new EventContext(new UserContext("u1"), new AssignToOp("o1", "oa1")));
        assertTrue(pap.policy().graph().nodeExists("hello world"));
    }
}