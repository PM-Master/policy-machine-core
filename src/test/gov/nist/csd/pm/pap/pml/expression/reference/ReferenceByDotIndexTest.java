package gov.nist.csd.pm.pap.pml.expression.reference;


import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.MapValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceByDotIndexTest {

    @Test
    void testGetType() throws PMException {
        ReferenceByDotIndex a = new ReferenceByDotIndex(new ReferenceByID("a"), "b");
        VisitorContext visitorContext = new VisitorContext(GlobalScope.forCompile(new MemoryPAP()));
        Type expected =  Type.array(Type.string());
        visitorContext.scope().addVariable("a", new Variable("a", Type.map(Type.string(), expected), false));

        assertEquals(
                expected,
                a.getType(visitorContext.scope())
        );
    }

    @Test
    void testExecute() throws PMException {
        ReferenceByDotIndex a = new ReferenceByDotIndex(new ReferenceByID("a"), "b");
        ExecutionContext executionContext = new ExecutionContext(new UserContext(""), GlobalScope.forExecute(new MemoryPAP()));
        ArrayValue expected = new ArrayValue(List.of(new StringValue("1"), new StringValue("2")), Type.string());
        MapValue mapValue = new MapValue(
                Map.of(new StringValue("b"), expected), Type.string(), Type.array(Type.string()));
        executionContext.scope().addVariable("a", mapValue);

        PAP pap = new MemoryPAP();
        Value actual = a.execute(executionContext, pap);
        assertEquals(expected, actual);
    }

    @Test
    void testIndexChain() throws PMException {
        String pml = """
                a := {
                    "b": {
                        "c": {
                            "d": "e"
                        }  
                    }
                }
                
                create policy class a.b.c.d
                """;
        PAP pap = new MemoryPAP();
        pap.modify().graph().createPolicyClass("pc1", new HashMap<>());
        pap.modify().graph().createUserAttribute("ua1", new HashMap<>(), List.of("pc1"));
        pap.modify().graph().createUserAttribute("u1", new HashMap<>(), List.of("ua1"));
        PMLExecutor.compileAndExecutePML(pap, new UserContext("u1"), pml);

        assertTrue(pap.query().graph().nodeExists("e"));
    }


}