package gov.nist.csd.pm.pap.pml.statement;

import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pdp.UserContext;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import org.junit.jupiter.api.Test;

class FunctionReturnStatementTest {

    @Test
    void testReturnValueIsUnwrapped() throws PMException {
        String pml = """
                function f1() string {
                    return f2()
                }
                
                function f2() string {
                    return "test"
                }
                
                create policy class f1()
                """;
        MemoryPolicyStore store = new MemoryPolicyStore();
        PMLExecutor.compileAndExecutePML(store, new UserContext(), pml);

    }

}