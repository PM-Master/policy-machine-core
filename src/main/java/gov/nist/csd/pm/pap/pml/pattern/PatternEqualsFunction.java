package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.common.op.pattern.Equals;
import gov.nist.csd.pm.impl.memory.pap.MemoryPolicyStore;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;
import gov.nist.csd.pm.pdp.UserContext;

import java.util.List;

public class PatternEqualsFunction extends PMLPatternFunctionStmt {

    public static void main(String[] args) throws PMException {
        PatternEqualsFunction patternEqualsFunction = new PatternEqualsFunction();
        patternEqualsFunction.setValueToMatch(new StringValue("test"));

        MemoryPolicyStore ps = new MemoryPolicyStore();
        ExecutionContext executionContext = new ExecutionContext(
                new UserContext(""),
                GlobalScope.forExecute(ps, patternEqualsFunction)
        );

        executionContext.scope().local().addOrOverwriteVariable("o", new StringValue("test1"));

        Value value = patternEqualsFunction.getFunctionExecutor().exec(executionContext, ps);
        System.out.println(value);
    }

    public PatternEqualsFunction() {
        super("patternEquals", Type.bool(), List.of(new FormalArgument("o", Type.any())), new Equals(""));

        setFunctionExecutor((ctx, policy) -> {
            Value o = ctx.scope().getVariable("o");
            if (o.equals(valueToMatch)) {
                return new BoolValue(true);
            }

            return new BoolValue(false);
        });
    }
}
