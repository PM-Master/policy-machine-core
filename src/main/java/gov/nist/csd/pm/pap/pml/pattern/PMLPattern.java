package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.op.pattern.Pattern;
import gov.nist.csd.pm.common.op.pattern.ReferencedPolicyEntities;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.impl.memory.pdp.MemoryGraphReviewer;
import gov.nist.csd.pm.pap.GraphReview;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.value.Value;

public class PMLPattern extends Pattern<Object> {

    public static void main(String[] args) throws PMException {

        PMLPattern pattern = new PMLPattern(new PatternEqualsFunction());
        boolean matches = pattern.matches("test", new MemoryGraphReviewer(new MemoryPolicyStore()));
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
    public boolean matches(Object value, GraphReview graphReview) throws PMException {
        patternFunctionStmt.setValueToMatch(Value.fromObject(value));

        ExecutionContext executionContext = new ExecutionContext(
                null,
                GlobalScope.forExecute(new MemoryPolicyStore())
        );
        executionContext.scope().local().addOrOverwriteVariable("o", Value.fromObject(""));

        Value ret = patternFunctionStmt.getFunctionExecutor().exec(executionContext, new MemoryPolicyStore());
        return ret.getBooleanValue();
    }

    @Override
    public ReferencedPolicyEntities getReferencedPolicyEntities() {
        return null;
    }
}
