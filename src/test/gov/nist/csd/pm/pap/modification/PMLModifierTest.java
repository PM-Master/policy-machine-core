package gov.nist.csd.pm.pap.modification;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.exception.PMLConstantAlreadyDefinedException;
import gov.nist.csd.pm.pap.exception.PMLConstantNotDefinedException;
import gov.nist.csd.pm.pap.exception.PMLFunctionAlreadyDefinedException;
import gov.nist.csd.pm.pap.exception.PMLFunctionNotDefinedException;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.CreateNonPCStatement;
import gov.nist.csd.pm.pap.pml.statement.CreatePolicyStatement;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static gov.nist.csd.pm.common.graph.node.NodeType.OA;
import static gov.nist.csd.pm.common.graph.node.NodeType.UA;
import static org.junit.jupiter.api.Assertions.*;

public abstract class PMLModifierTest extends ModificationTest {

    @Nested
    class CreateFunction {

        FunctionDefinitionStatement testFunc = new FunctionDefinitionStatement.Builder("testFunc")
                .returns(Type.string())
                .args(
                        new FormalArg("arg1", Type.string()),
                        new FormalArg("arg2", Type.array(Type.string()))
                )
                .body(
                        new CreatePolicyStatement(new StringLiteral("pc1")),
                        new CreateNonPCStatement(
                                new StringLiteral("ua1"),
                                UA,
                                new ArrayLiteral(new Expression[]{new StringLiteral("pc1")}, Type.string())
                        ),
                        new CreateNonPCStatement(
                                new StringLiteral("oa1"),
                                OA,
                                new ArrayLiteral(new Expression[]{new StringLiteral("pc1")}, Type.string())
                        )
                )
                .build();

        @Test
        void testPMLFunctionAlreadyDefinedException() throws PMException {
            pap.modify().pml().createFunction(testFunc);
            assertThrows(PMLFunctionAlreadyDefinedException.class, () -> pap.modify().pml().createFunction(testFunc));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().pml().createFunction(testFunc);
            assertTrue(pap.query().pml().getFunctions().containsKey(testFunc.getSignature().getFunctionName()));
            FunctionDefinitionStatement actual = pap.query().pml().getFunctions().get(testFunc.getSignature().getFunctionName());
            assertEquals(testFunc, actual);
        }
    }

    @Nested
    class DeleteFunction {

        @Test
        void testNonExistingFunctionDoesNotThrowException() {
            assertDoesNotThrow(() -> pap.modify().pml().deleteFunction("func"));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().pml().createFunction(new FunctionDefinitionStatement.Builder("testFunc").returns(Type.voidType()).build());
            assertTrue(pap.query().pml().getFunctions().containsKey("testFunc"));
            pap.modify().pml().deleteFunction("testFunc");
            assertFalse(pap.query().pml().getFunctions().containsKey("testFunc"));
        }
    }

    @Nested
    class GetFunctions {

        @Test
        void testSuccess() throws PMException {
            FunctionDefinitionStatement testFunc1 = new FunctionDefinitionStatement.Builder("testFunc1").returns(Type.voidType()).build();
            FunctionDefinitionStatement testFunc2 = new FunctionDefinitionStatement.Builder("testFunc2").returns(Type.voidType()).build();

            pap.modify().pml().createFunction(testFunc1);
            pap.modify().pml().createFunction(testFunc2);

            Map<String, FunctionDefinitionStatement> functions = pap.query().pml().getFunctions();
            assertTrue(functions.containsKey("testFunc1"));
            FunctionDefinitionStatement actual = functions.get("testFunc1");
            assertEquals(testFunc1, actual);

            assertTrue(functions.containsKey("testFunc2"));
            actual = functions.get("testFunc2");
            assertEquals(testFunc2, actual);
        }

    }

    @Nested
    class GetFunction {

        @Test
        void testPMLFunctionNotDefinedException() {
            assertThrows(PMLFunctionNotDefinedException.class, () -> pap.query().pml().getFunction("func1"));
        }

        @Test
        void testSuccess() throws PMException {
            FunctionDefinitionStatement testFunc1 = new FunctionDefinitionStatement.Builder("testFunc1").returns(Type.voidType()).build();
            FunctionDefinitionStatement testFunc2 = new FunctionDefinitionStatement.Builder("testFunc2").returns(Type.voidType()).build();

            pap.modify().pml().createFunction(testFunc1);
            pap.modify().pml().createFunction(testFunc2);

            Map<String, FunctionDefinitionStatement> functions = pap.query().pml().getFunctions();
            assertTrue(functions.containsKey("testFunc1"));
            FunctionDefinitionStatement actual = functions.get("testFunc1");
            assertEquals(testFunc1, actual);

            assertTrue(functions.containsKey("testFunc2"));
            actual = functions.get("testFunc2");
            assertEquals(testFunc2, actual);
        }

    }

    @Nested
    class CreateConstant {

        @Test
        void testPMLConstantAlreadyDefinedException() throws PMException {
            pap.modify().pml().createConstant("const1", new StringValue("test"));
            assertThrows(
                    PMLConstantAlreadyDefinedException.class,
                    () -> pap.modify().pml().createConstant("const1", new StringValue("test")));
        }

        @Test
        void testSuccess() throws PMException {
            StringValue expected = new StringValue("test");

            pap.modify().pml().createConstant("const1", expected);
            assertTrue(pap.query().pml().getConstants().containsKey("const1"));
            Value actual = pap.query().pml().getConstants().get("const1");
            assertEquals(expected, actual);
        }
    }

    @Nested
    class DeleteConstant {

        @Test
        void testNonExistingConstantDoesNotThrowException() {
            assertDoesNotThrow(() -> pap.modify().pml().deleteConstant("const1"));
        }

        @Test
        void testSuccess() throws PMException {
            pap.modify().pml().createConstant("const1", new StringValue("test"));
            assertTrue(pap.query().pml().getConstants().containsKey("const1"));
            pap.modify().pml().deleteConstant("const1");
            assertFalse(pap.query().pml().getConstants().containsKey("const1"));
        }
    }

    @Nested
    class GetConstants {

        @Test
        void success() throws PMException {
            StringValue const1 = new StringValue("test1");
            StringValue const2 = new StringValue("test2");

            pap.modify().pml().createConstant("const1", const1);
            pap.modify().pml().createConstant("const2", const2);

            Map<String, Value> constants = pap.query().pml().getConstants();
            assertTrue(constants.containsKey("const1"));
            Value actual = constants.get("const1");
            assertEquals(const1, actual);

            assertTrue(constants.containsKey("const2"));
            actual = constants.get("const2");
            assertEquals(const2, actual);
        }
    }


    @Nested
    class GetConstant {

        @Test
        void testPMLConstantNotDefinedException() {
            assertThrows(PMLConstantNotDefinedException.class, () -> pap.query().pml().getConstant("const1"));
        }

        @Test
        void success() throws PMException {
            StringValue const1 = new StringValue("test1");
            StringValue const2 = new StringValue("test2");

            pap.modify().pml().createConstant("const1", const1);
            pap.modify().pml().createConstant("const2", const2);

            Map<String, Value> constants = pap.query().pml().getConstants();
            assertTrue(constants.containsKey("const1"));
            Value actual = constants.get("const1");
            assertEquals(const1, actual);

            assertTrue(constants.containsKey("const2"));
            actual = constants.get("const2");
            assertEquals(const2, actual);
        }
    }
}