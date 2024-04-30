package gov.nist.csd.pm.epp;

import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.common.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.common.obligation.event.subject.AnyUserSubject;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.op.graph.CreateObjectAttributeOp;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.statement.CreateNonPCStatement;
import gov.nist.csd.pm.pap.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.pdp.PDP;
import gov.nist.csd.pm.common.graph.nodes.NodeType;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.event.EventPattern;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.pdp.AdminAccessRights.*;
import static gov.nist.csd.pm.common.obligation.event.Performs.events;
import static org.junit.jupiter.api.Assertions.*;

class EPPTest {

    @Test
    void test() throws PMException {
        MemoryPolicyStore ps = new MemoryPolicyStore();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        PDP pdp = new PDP(pap);
        EPP epp = new EPP(pdp, pap);

        String pml = """
                create pc "pc1"
                create oa "oa1" assign to ["pc1"]
                create ua "ua1" assign to ["pc1"]
                create u "u1" assign to ["ua1"]
                associate "ua1" and "oa1" with ["*"]
                associate "ua1" and POLICY_CLASS_TARGETS with ["*"]
                create obligation "test" {
                    create rule "rule1"
                    when any user
                    performs ["create_object_attribute"]
                    on ["oa1"]
                    do(evtCtx) {
                        create policy class "pc2"
                    }
                }
                """;
        pap.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        assertTrue(pap.policy().graph().nodeExists("pc1"));
        assertTrue(pap.policy().graph().nodeExists("oa1"));

        pdp.runTx(new UserContext("u1"), (txPDP) -> txPDP.graph().createObjectAttribute("oa2",
                new HashMap<>(),
                List.of("oa1")));

        assertTrue(pap.policy().graph().nodeExists("pc2"));

    }

    @Test
    void testAccessingEventContextInResponse() throws PMException {
        MemoryPolicyStore ps = new MemoryPolicyStore();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        PDP pdp = new PDP(pap);
        EPP epp = new EPP(pdp, pap);

        String pml = """                
                create pc "pc1"
                create ua "ua1" assign to ["pc1"]
                create u "u1" assign to ["ua1"]
                create oa "oa1" assign to ["pc1"]
                
                associate "ua1" and "oa1" with ["*a"]
                associate "ua1" and POLICY_CLASS_TARGETS with [create_policy_class]
                
                create obligation "test" {
                    create rule "rule1"
                    when any user
                    performs ["create_object_attribute"]
                    on ["oa1"]
                    do(ctx) {
                        create policy class ctx.opName
                        target := ctx["target"]
                        
                        create policy class ctx["opName"] + "_test"
                        set properties of ctx["opName"] to {"key": target}
                        
                        userCtx := ctx["userCtx"]
                        create policy class ctx["user"] + "_test"
                    }
                }
                """;
        pap.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        pdp.runTx(new UserContext("u1"), (txPDP) -> txPDP.graph().createObjectAttribute("oa2", new HashMap<>(),
                List.of("oa1")));
        assertTrue(pap.policy().graph().getPolicyClasses().containsAll(Arrays.asList(
                "pc1", "create_object_attribute", "oa2_test", "u1_test"
        )));
    }

    @Test
    void testErrorInEPPResponse() throws PMException { MemoryPolicyStore ps = new MemoryPolicyStore();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);
        PDP pdp = new PDP(pap);
        EPP epp = new EPP(pdp, pap);

        pap.runTx((txPAP) -> {
            txPAP.policy().graph().createPolicyClass("pc1", new HashMap<>());
            txPAP.policy().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            txPAP.policy().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
            txPAP.policy().graph().associate("ua2", "ua1", new AccessRightSet("*"));
            txPAP.policy().graph().associate("ua2", AdminPolicyNode.OBLIGATIONS_TARGET.nodeName(), new AccessRightSet("*"));
            txPAP.policy().graph().createObjectAttribute("oa1", new HashMap<>(),  List.of("pc1"));
            txPAP.policy().graph().createUser("u1", new HashMap<>(),  List.of("ua1", "ua2"));
            txPAP.policy().graph().createObject("o1", new HashMap<>(),  List.of("oa1"));
            txPAP.policy().graph().associate("ua1", AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(),
                    new AccessRightSet(CREATE_OBLIGATION));
            txPAP.policy().graph().associate("ua1", "oa1", new AccessRightSet(CREATE_OBJECT));
            txPAP.policy().graph().associate("ua1", AdminPolicyNode.OBLIGATIONS_TARGET.nodeName(), new AccessRightSet("*"));
        });

        pdp.runTx(new UserContext("u1"), (policy) -> {
            policy.obligations().create(new UserContext("u1"), "test",
                    new Rule("rule1",
                            new EventPattern(new AnyUserSubject(), events(CREATE_OBJECT_ATTRIBUTE)),
                            new Response("evtCtx", List.of(
                                    new CreateNonPCStatement(
                                            new StringLiteral("o2"),
                                            NodeType.O,
                                            new ArrayLiteral(new Expression[]{new StringLiteral("oa1")}, Type.string())
                                    ),
                                    new CreatePolicyStatement(new StringLiteral("pc2"))
                            ))
                    )
            );
        });

        EventContext eventCtx = new EventContext(
                new UserContext("u1"),
                new CreateObjectAttributeOp(
                        "oa2",
                        new HashMap<>(),
                        List.of("pc1")
                )
        );
        assertThrows(PMException.class, () -> {
            epp.getEventProcessor().processEvent(eventCtx);
        });

        assertFalse(pap.policy().graph().nodeExists("o2"));
        assertFalse(pap.policy().graph().nodeExists("pc2"));
    }

    @Test
    void testCustomFunctionInResponse() throws PMException {
        MemoryPolicyStore ps = new MemoryPolicyStore();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);

        FunctionDefinitionStatement testFunc = new FunctionDefinitionStatement.Builder("testFunc")
                .returns(Type.voidType())
                .executor((ctx, policy) -> {
                    policy.graph().createPolicyClass("test", new HashMap<>());

                    return new VoidValue();
                })
                .build();

        PDP pdp = new PDP(pap);
        EPP epp = new EPP(pdp, pap, testFunc);

        String pml = """                
                create pc "pc1"
                create ua "ua1" assign to ["pc1"]
                create u "u1" assign to ["ua1"]
                create oa "oa1" assign to ["pc1"]
                
                associate "ua1" and "oa1" with ["*a"]
                associate "ua1" and POLICY_CLASS_TARGETS with [create_policy_class]
                
                create obligation "test" {
                    create rule "rule1"
                    when any user
                    performs ["create_object_attribute"]
                    on ["oa1"]
                    do(evtCtx) {
                        testFunc()
                    }
                }
                """;
        pap.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer(testFunc));

        pdp.runTx(new UserContext("u1"), (txPDP) -> txPDP.graph().createObjectAttribute("oa2", new HashMap<>(),
                List.of("oa1")));
        assertTrue(pap.policy().graph().nodeExists("test"));
    }

    @Test
    void testReturnInResponse() throws PMException {
        MemoryPolicyStore ps = new MemoryPolicyStore();
        MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
        PAP pap = new PAP(ps, pr);

        PDP pdp = new PDP(pap);
        EPP epp = new EPP(pdp, pap);

        String pml = """                
                create pc "pc1"
                create ua "ua1" assign to ["pc1"]
                create u "u1" assign to ["ua1"]
                create oa "oa1" assign to ["pc1"]
                
                associate "ua1" and "oa1" with ["*a"]
                associate "ua1" and POLICY_CLASS_TARGETS with [create_policy_class]
                
                create obligation "test" {
                    create rule "rule1"
                    when any user
                    performs ["create_object_attribute"]
                    on ["oa1"]
                    do(evtCtx) {
                        if true {
                            return
                        }
                        
                        create policy class "test"
                    }
                }
                """;
        pap.policy().deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        pdp.runTx(new UserContext("u1"), (txPDP) -> txPDP.graph().createObjectAttribute("oa2", new HashMap<>(),
                List.of("oa1")));
        assertFalse(pap.policy().graph().nodeExists("test"));
    }
}