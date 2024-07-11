package gov.nist.csd.pm.pap.pml.statement;


import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.impl.memory.pap.MemoryPAP;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.query.UserContext;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionDefinitionStatementTest {

    @Test
    void testToFormattedString() {
        FunctionDefinitionStatement stmt = new FunctionDefinitionStatement.Builder("func1")
                .returns(Type.string())
                .args(
                        new FormalArg("a", Type.string()),
                        new FormalArg("b", Type.bool()),
                        new FormalArg("c", Type.array(Type.string()))
                )
                .body(
                        new FunctionReturnStatement(new StringLiteral("test"))
                )
                .build();

        assertEquals("""
                             function func1(string a, bool b, []string c) string {
                                 return "test"
                             }""",
                     stmt.toFormattedString(0));

        assertEquals("""
                                 function func1(string a, bool b, []string c) string {
                                     return "test"
                                 }
                             """,
                     stmt.toFormattedString(1) + "\n");
    }

    @Test
    void testToFormattedStringVoidReturn() {
        FunctionDefinitionStatement stmt = new FunctionDefinitionStatement.Builder("func1")
                .returns(Type.voidType())
                .args(
                        new FormalArg("a", Type.string()),
                        new FormalArg("b", Type.bool()),
                        new FormalArg("c", Type.array(Type.string()))
                )
                .body(
                        new FunctionReturnStatement()
                )
                .build();

        assertEquals("""
                             function func1(string a, bool b, []string c) {
                                 return
                             }""",
                     stmt.toFormattedString(0));

        assertEquals("""
                                 function func1(string a, bool b, []string c) {
                                     return
                                 }
                             """,
                     stmt.toFormattedString(1) + "\n");
    }

    @Test
    void testFormalArgOverwritesVariable()
            throws PMException {
        String pml = """
                var a = "test"
                var b = "test2"
                func1(a, b)
                
                function func1(string a, string b) {
                    create policy class a
                    create policy class b
                }
                """;
        PAP pap = new MemoryPAP();
        PMLExecutor.compileAndExecutePML(pap, new UserContext(""), pml);

        assertTrue(pap.query().graph().nodeExists("test"));
        assertTrue(pap.query().graph().nodeExists("test2"));
    }

    @Test
    void testInvokeFromDefinition() throws PMException {
        String pml = """
                function f1(string a) {
                    create policy class a
                }
                
                function f2() {
                    a := "test"
                    f1(a)
                }
                
                f2()
                """;
        PAP pap = new MemoryPAP();
        PMLExecutor.compileAndExecutePML(pap, new UserContext(""), pml);

        assertTrue(pap.query().graph().nodeExists("test"));
    }

    @Test
    void testInvokeFromFunctionUsingConstant() throws PMException {
        String pml = """
                const x = "x"
                
                func1()
                
                function func1() {
                    func2()
                }
                
                function func2() {
                    create policy class x
                }
                """;
        PAP pap = new MemoryPAP();
        PMLExecutor.compileAndExecutePML(pap, new UserContext(""), pml);
        assertTrue(pap.query().graph().nodeExists("x"));
    }
}