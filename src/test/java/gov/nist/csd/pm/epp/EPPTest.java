package gov.nist.csd.pm.epp;

import gov.nist.csd.pm.common.graph.node.NodeType;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.obligation.EventContext;
import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.AdminPolicyNode;
import gov.nist.csd.pm.pap.serialization.pml.PMLDeserializer;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.graph.CreateObjectAttributeOp;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.statement.CreateNonPCStatement;
import gov.nist.csd.pm.pap.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.VoidValue;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pdp.PDP;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static gov.nist.csd.pm.pap.op.AdminAccessRights.*;
import static gov.nist.csd.pm.pap.pml.pattern.AnyPatternFunction.pAny;
import static gov.nist.csd.pm.pap.pml.pattern.EqualsPatternFunction.pEquals;
import static org.junit.jupiter.api.Assertions.*;

class EPPTest {

    @Test
    void test() throws PMException {
        MemoryPAP pap = new MemoryPAP();
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
                    when subject => pAny()
                    performs op => pEquals("create_object_attribute")
                    on opnd1 => pAny()
                        opnd2 => pContains("oa1")
                    do(evtCtx) {
                        create policy class "pc2"
                    }
                }
                """;
        pap.deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        assertTrue(pap.query().graph().nodeExists("pc1"));
        assertTrue(pap.query().graph().nodeExists("oa1"));

        pdp.runTx(new UserContext("u1"), (txPDP) -> txPDP.modify().graph().createObjectAttribute("oa2",
                new HashMap<>(),
                List.of("oa1")));

        assertTrue(pap.query().graph().nodeExists("pc2"));

    }

    @Test
    void testAccessingEventContextInResponse() throws PMException {
        MemoryPAP pap = new MemoryPAP();
        PDP pdp = new PDP(pap);
        EPP epp = new EPP(pdp, pap);

        String pml = """                
                create pc "pc1"
                create ua "ua1" assign to ["pc1"]
                create u "u1" assign to ["ua1"]
                create oa "oa1" assign to ["pc1"]
                
                associate "ua1" and "oa1" with ["*a"]
                associate "ua1" and POLICY_CLASS_TARGETS with ["*a"]
                
                create obligation "test" {
                    create rule "rule1"
                    when subject => pAny()
                    performs op => pEquals("create_object_attribute")
                    on opnd1 => pAny()
                        opnd2 => pContains("oa1")
                    do(ctx) {
                        create policy class ctx.opName
                        op := ctx.op.name
                        
                        create policy class op + "_test"
                        set properties of op + "_test" to {"key": op}
                        
                        userCtx := ctx["userCtx"]
                        create policy class ctx["userCtx"]["user"] + "_test"
                    }
                }
                """;
        pap.deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        pdp.runTx(new UserContext("u1"), (txPDP) -> txPDP.modify().graph().createObjectAttribute("oa2", new HashMap<>(),
                List.of("oa1")));
        assertTrue(pap.query().graph().getPolicyClasses().containsAll(Arrays.asList(
                "pc1", "oa2_test", "oa2_test", "u1_test"
        )));
    }

    @Test
    void testErrorInEPPResponse() throws PMException {
        MemoryPAP pap = new MemoryPAP();
        PDP pdp = new PDP(pap);
        EPP epp = new EPP(pdp, pap);

        pap.runTx((txPAP) -> {
            txPAP.modify().graph().createPolicyClass("pc1", new HashMap<>());
            txPAP.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
            txPAP.modify().graph().createUserAttribute("ua2", new HashMap<>(), List.of("pc1"));
            txPAP.modify().graph().associate("ua2", "ua1", new AccessRightSet("*"));
            txPAP.modify().graph().associate("ua2", AdminPolicyNode.OBLIGATIONS_TARGET.nodeName(), new AccessRightSet("*"));
            txPAP.modify().graph().createObjectAttribute("oa1", new HashMap<>(),  List.of("pc1"));
            txPAP.modify().graph().createUser("u1", new HashMap<>(),  List.of("ua1", "ua2"));
            txPAP.modify().graph().createObject("o1", new HashMap<>(),  List.of("oa1"));
            txPAP.modify().graph().associate("ua1", AdminPolicyNode.ADMIN_POLICY_TARGET.nodeName(),
                    new AccessRightSet(CREATE_OBLIGATION));
            txPAP.modify().graph().associate("ua1", "oa1", new AccessRightSet(CREATE_OBJECT));
            txPAP.modify().graph().associate("ua1", AdminPolicyNode.OBLIGATIONS_TARGET.nodeName(), new AccessRightSet("*"));
        });

        pdp.runTx(new UserContext("u1"), (policy) -> {
            policy.modify().obligations().create(new UserContext("u1"), "test",
                    List.of(new Rule("rule1",
                            new EventPattern(pAny("subject"), pEquals("op", new StringValue(CREATE_OBJECT_ATTRIBUTE))),
                            new Response("evtCtx", List.of(
                                    new CreateNonPCStatement(
                                            new StringLiteral("o2"),
                                            NodeType.O,
                                            new ArrayLiteral(new Expression[]{new StringLiteral("oa1")}, Type.string())
                                    ),
                                    new CreatePolicyStatement(new StringLiteral("pc2"))
                            ))
                    ))
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

        assertFalse(pap.query().graph().nodeExists("o2"));
        assertFalse(pap.query().graph().nodeExists("pc2"));
    }

    @Test
    void testCustomFunctionInResponse() throws PMException {
        MemoryPAP pap = new MemoryPAP();

        FunctionDefinitionStatement testFunc = new FunctionDefinitionStatement.Builder("testFunc")
                .returns(Type.voidType())
                .executor((ctx, policy) -> {
                    policy.modify().graph().createPolicyClass("test", new HashMap<>());

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
                    when subject => pAny()
                    performs op => pEquals("create_object_attribute")
                    on o1 => pAny()
                        o2 => pContains("oa1")
                    do(evtCtx) {
                        testFunc()
                    }
                }
                """;
        pap.deserialize(new UserContext("u1"), pml, new PMLDeserializer(testFunc));

        pdp.runTx(new UserContext("u1"), (txPDP) -> txPDP.modify().graph().createObjectAttribute("oa2", new HashMap<>(),
                List.of("oa1")));
        assertTrue(pap.query().graph().nodeExists("test"));
    }

    @Test
    void testReturnInResponse() throws PMException {
        MemoryPAP pap = new MemoryPAP();

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
                    when subject => pAny()
                    performs op => pEquals("create_object_attribute")
                    on o1 => pAny()
                        o2 => pContains("oa1")
                    do(evtCtx) {
                        if true {
                            return
                        }
                        
                        create policy class "test"
                    }
                }
                """;
        pap.deserialize(new UserContext("u1"), pml, new PMLDeserializer());

        pdp.runTx(new UserContext("u1"), (txPDP) -> txPDP.modify().graph().createObjectAttribute("oa2", new HashMap<>(),
                List.of("oa1")));
        assertFalse(pap.query().graph().nodeExists("test"));
    }
}