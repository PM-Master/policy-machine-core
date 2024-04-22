package gov.nist.csd.pm.pap.events;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.serialization.pml.PMLSerializer;
import gov.nist.csd.pm.pap.op.graph.CreatePolicyClassEvent;
import gov.nist.csd.pm.pap.op.obligations.CreateObligationEvent;
import gov.nist.csd.pm.pap.op.prohibitions.CreateProhibitionEvent;
import gov.nist.csd.pm.pap.op.userdefinedpml.CreateFunctionEvent;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.AccessRightSet;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.obligation.event.subject.AnyUserSubject;
import gov.nist.csd.pm.common.obligation.event.EventPattern;
import gov.nist.csd.pm.common.obligation.event.Performs;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SerializationTest {

    @Test
    void testGraphEvent() {
        CreatePolicyClassEvent expected = new CreatePolicyClassEvent("pc1", Map.of("a", "b"));
        byte[] serialize = SerializationUtils.serialize(expected);
        CreatePolicyClassEvent actual = SerializationUtils.deserialize(serialize);
        assertEquals(expected, actual);
    }

    @Test
    void testProhibitionEvent() {
        CreateProhibitionEvent expected = new CreateProhibitionEvent(
                "pro1",
                new ProhibitionSubject("u1", ProhibitionSubject.Type.USER),
                new AccessRightSet("read"),
                false,
                List.of(new ContainerCondition("oa1", true), new ContainerCondition("oa2", false))
        );
        byte[] serialize = SerializationUtils.serialize(expected);
        CreateProhibitionEvent actual = SerializationUtils.deserialize(serialize);
        assertEquals(expected, actual);
    }

    @Test
    void testObligationEvent() {
        CreateObligationEvent expected = new CreateObligationEvent(
                new UserContext("u1"),
                "obl",
                List.of(
                        new Rule(
                                "rule1",
                                new EventPattern(
                                        new AnyUserSubject(),
                                        new Performs("test_event")
                                ),
                                new Response("evtCtx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("test_pc"))
                                ))
                        )
                )
        );
        byte[] serialize = SerializationUtils.serialize(expected);
        CreateObligationEvent actual = SerializationUtils.deserialize(serialize);
        assertEquals(expected, actual);
    }

    @Test
    void testFuncExecDoestNotSerialize() throws PMException {
        MemoryPolicyStore memoryPolicyStore = new MemoryPolicyStore();
        CreateFunctionEvent createFunctionEvent = new CreateFunctionEvent(new FunctionDefinitionStatement.Builder("test_func")
                                                                                  .returns(Type.string())
                                                                                  .args(
                                                                                          new FormalArgument("arg1", Type.string())
                                                                                  )
                                                                                  .executor((ctx, policy) -> new StringValue("hello world"))
                                                                                  .build());

        createFunctionEvent.apply(memoryPolicyStore);

        assertThrows(RuntimeException.class, () -> memoryPolicyStore.serialize(new PMLSerializer()));
    }

}
