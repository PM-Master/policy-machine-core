package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.impl.memory.pdp.MemoryPolicyReviewer;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FunctionTest {

    @Test
    void testElseIfNotAllPathsReturn() {
        String pml = """
                function fun(string a) string {
                    if equals(a, "a") {
                        return "a"
                    } else if equals(a, "b") {
                        return "b"
                    }
                }
                """;

        PMLCompilationException e = assertThrows(PMLCompilationException.class, () -> {
            MemoryPolicyModifier ps = new MemoryPolicyModifier();
            MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
            PAP pap = new PAP(ps, pr);
            PMLExecutor.compileAndExecutePML(pap.policy(), new UserContext("u1"), pml);
        });
        assertEquals("not all conditional paths return", e.getErrors().get(0).errorMessage());
    }

    @Test
    void testElseAllPathsReturn() {
        String pml2 = """
                function fun(string a) string {
                    if equals(a, "a") {
                        return "a"
                    } else if equals(a, "b") {
                        return "b"
                    } else {
                        return "c"
                    }
                }
                """;

        assertDoesNotThrow(() -> {
            MemoryPolicyModifier ps = new MemoryPolicyModifier();
            MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
            PAP pap = new PAP(ps, pr);
            PMLExecutor.compileAndExecutePML(pap.policy(), new UserContext("u1"), pml2);
        });
    }

    @Test
    void testElseWithNoElseIfAllPathsReturn() {
        String pml2 = """
                function fun(string a) string {
                    if equals(a, "a") {
                        return "a"
                    } else {
                        return "b"
                    }
                }
                """;

        assertDoesNotThrow(() -> {
            MemoryPolicyModifier ps = new MemoryPolicyModifier();
            MemoryPolicyReviewer pr = new MemoryPolicyReviewer(ps);
            PAP pap = new PAP(ps, pr);
            PMLExecutor.compileAndExecutePML(pap.policy(), new UserContext("u1"), pml2);
        });
    }

}
