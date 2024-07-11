package gov.nist.csd.pm.pap.op;

import gov.nist.csd.pm.common.obligation.EventPattern;
import gov.nist.csd.pm.common.obligation.Response;
import gov.nist.csd.pm.common.obligation.Rule;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.graph.CreatePolicyClassOp;
import gov.nist.csd.pm.pap.op.obligation.CreateObligationOp;
import gov.nist.csd.pm.pap.op.prohibition.CreateProhibitionOp;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.common.prohibition.ContainerCondition;
import gov.nist.csd.pm.common.prohibition.ProhibitionSubject;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.query.UserContext;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static gov.nist.csd.pm.pap.pml.pattern.AnyPatternFunction.pAny;
import static gov.nist.csd.pm.pap.pml.pattern.EqualsPatternFunction.pEquals;
import static org.junit.jupiter.api.Assertions.*;

public class SerializationTest {

    @Test
    void testGraphOp() {
        CreatePolicyClassOp expected = new CreatePolicyClassOp("pc1", Map.of("a", "b"));
        byte[] serialize = SerializationUtils.serialize(expected);
        CreatePolicyClassOp actual = SerializationUtils.deserialize(serialize);
        assertEquals(expected, actual);
    }

    @Test
    void testProhibitionOp() {
        CreateProhibitionOp expected = new CreateProhibitionOp(
                "pro1",
                new ProhibitionSubject("u1", ProhibitionSubject.Type.USER),
                new AccessRightSet("read"),
                false,
                List.of(new ContainerCondition("oa1", true), new ContainerCondition("oa2", false))
        );
        byte[] serialize = SerializationUtils.serialize(expected);
        CreateProhibitionOp actual = SerializationUtils.deserialize(serialize);
        assertEquals(expected, actual);
    }

    @Test
    void testObligationOp() throws PMException {
        CreateObligationOp expected = new CreateObligationOp(
                new UserContext("u1"),
                "obl",
                List.of(
                        new Rule(
                                "rule1",
                                new EventPattern(
                                        pAny("subject"),
                                        pEquals("op", new StringValue("test_event"))
                                ),
                                new Response("evtCtx", List.of(
                                        new CreatePolicyStatement(new StringLiteral("test_pc"))
                                ))
                        )
                )
        );
        byte[] serialize = SerializationUtils.serialize(expected);
        CreateObligationOp actual = SerializationUtils.deserialize(serialize);
        assertEquals(expected, actual);
    }
}
