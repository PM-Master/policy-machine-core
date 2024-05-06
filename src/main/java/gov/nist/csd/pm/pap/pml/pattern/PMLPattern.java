package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.op.pattern.ReferencedPolicyEntities;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyModifier;
import gov.nist.csd.pm.impl.memory.pdp.MemoryGraphReviewer;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pap.query.PolicyQuery;

public class PMLPattern extends Pattern<Object> {

    public static void main(String[] args) throws PMException {

        PMLPattern pattern = new PMLPattern(new PatternEqualsFunction());
        boolean matches = pattern.matches("test", new MemoryGraphReviewer(new MemoryPolicyModifier()));
        System.out.println(matches);
    }

    private PMLPatternFunctionStmt patternFunctionStmt;

    public PMLPattern(PMLPatternFunctionStmt patternFunctionStmt) {
        this.patternFunctionStmt = patternFunctionStmt;
    }


    /*
    patternFunctionStmt.pattern.matches
     */

    @Override
    public boolean matches(Object value, PolicyQuery querier) throws PMException {
        patternFunctionStmt.setValueToMatch(Value.fromObject(value));

        ExecutionContext executionContext = new ExecutionContext(
                null,
                GlobalScope.forExecute(new MemoryPolicyModifier())
        );
        executionContext.scope().local().addOrOverwriteVariable("o", Value.fromObject(""));

        Value ret = patternFunctionStmt.getFunctionExecutor().exec(executionContext, new MemoryPolicyModifier());
        return ret.getBooleanValue();
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        return null;
    }
}
