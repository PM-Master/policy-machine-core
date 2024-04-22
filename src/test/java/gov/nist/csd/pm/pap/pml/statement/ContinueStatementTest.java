package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContinueStatementTest {

    @Test
    void testSuccess() throws PMException {
        String pml = """
                foreach x in ["a", "b", "c"] {
                    if x == "b" {
                        continue
                    }         
                    
                    create policy class x         
                }
                """;
        MemoryPolicyStore store = new MemoryPolicyStore();
        PMLExecutor.compileAndExecutePML(store, new UserContext(""), pml);

        assertTrue(store.graph().nodeExists("a"));
        assertFalse(store.graph().nodeExists("b"));
        assertTrue(store.graph().nodeExists("c"));
    }

    @Test
    void testMultipleLevels() throws PMException {
        String pml = """
                foreach x in ["a", "b", "c"] {
                    if x == "b" {
                        if x == "b" {
                            continue
                        }   
                    }         
                    
                    create policy class x         
                }
                """;
        MemoryPolicyStore store = new MemoryPolicyStore();
        PMLExecutor.compileAndExecutePML(store, new UserContext(""), pml);

        assertTrue(store.graph().nodeExists("a"));
        assertFalse(store.graph().nodeExists("b"));
        assertTrue(store.graph().nodeExists("c"));
    }

    @Test
    void testToFormattedString() {
        ContinueStatement stmt = new ContinueStatement();

        assertEquals(
                "continue",
                stmt.toFormattedString(0)
        );
        assertEquals(
                "    continue",
                stmt.toFormattedString(1)
        );
    }

}