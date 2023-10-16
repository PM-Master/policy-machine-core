package gov.nist.csd.pm.policy.pml.statement;

import gov.nist.csd.pm.pap.memory.MemoryPolicyStore;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.UserContext;
import gov.nist.csd.pm.policy.pml.PMLExecutor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionReturnStatementTest {

    @Test
    void testReturnValueIsUnwrapped() throws PMException {
        String pml =
                "function f1() string {\n" +
                "    return f2()\n" +
                "}\n" +
                "\n" +
                "function f2() string {\n" +
                "    return \"test\"\n" +
                "}\n" +
                "\n" +
                "create policy class f1()";
        MemoryPolicyStore store = new MemoryPolicyStore();
        PMLExecutor.compileAndExecutePML(store, new UserContext(), pml);

    }

}