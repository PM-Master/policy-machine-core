package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.op.graph.CreateObjectAttributeOp;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.epp.EventContext;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.MapValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import org.junit.jupiter.api.Test;

import java.util.*;

import static gov.nist.csd.pm.pdp.AdminAccessRights.CREATE_OBJECT_ATTRIBUTE;
import static gov.nist.csd.pm.common.graph.nodes.Properties.NO_PROPERTIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValueTest {

    @Test
    void testStringToValue() throws PMException {
        Value value = Value.fromObject("test");
        assertTrue(value.getType().isString());
        assertEquals("test", value.getStringValue());
    }

    @Test
    void testArrayToValue() throws PMException {
        Value value = Value.fromObject(List.of("hello", "world"));
        assertTrue(value.getType().isArray());
        assertEquals(new StringValue("hello"), value.getArrayValue().get(0));
        assertEquals(new StringValue("world"), value.getArrayValue().get(1));
    }

    @Test
    void testBooleanToValue() throws PMException {
        Value value = Value.fromObject(true);
        assertTrue(value.getType().isBoolean());
        assertTrue(value.getBooleanValue());
    }

    @Test
    void testListToValue() throws PMException {
        Value value = Value.fromObject(Arrays.asList("hello", "world"));
        assertTrue(value.getType().isArray());
        assertEquals(new StringValue("hello"), value.getArrayValue().get(0));
        assertEquals(new StringValue("world"), value.getArrayValue().get(1));
    }

    @Test
    void testObjectToValue() throws PMException {
        EventContext testEventCtx = new EventContext(new UserContext("testUser"),
                new CreateObjectAttributeOp("testOA", NO_PROPERTIES, List.of("pc1")));

        Value objectToValue = Value.fromObject(testEventCtx);
        assertTrue(objectToValue.getType().isMap());

        Value key = new StringValue("userCtx");
        Value value = objectToValue.getMapValue().get(key);
        assertTrue(value.getType().isMap());
        assertEquals(
                Map.of(
                        new StringValue("user"), new StringValue("testUser"), new StringValue("process"),
                        new StringValue("")
                ),
                value.getMapValue()
        );

        key = new StringValue("target");
        value = objectToValue.getMapValue().get(key);
        assertTrue(value.getType().isString());
        assertEquals(
                "target123",
                value.getStringValue()
        );

        key = new StringValue("eventName");
        value = objectToValue.getMapValue().get(key);
        assertTrue(value.getType().isString());
        assertEquals(
                CREATE_OBJECT_ATTRIBUTE,
                value.getStringValue()
        );

        key = new StringValue("event");
        value = objectToValue.getMapValue().get(key);
        assertTrue(value.getType().isMap());
        assertEquals(
                Map.of(new StringValue("name"), new StringValue("testOA"),
                       new StringValue("type"), new StringValue("OA"),
                       new StringValue("properties"), new MapValue(new HashMap<>(), Type.string(), Type.string()),
                       new StringValue("parents"), new ArrayValue(List.of(new StringValue("pc1")), Type.string())
                ),
                value.getMapValue()
        );
    }
}
