package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.FunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public class PatternFunctionInvokeExpression extends FunctionInvokeExpression {
    public PatternFunctionInvokeExpression(String functionName, List<Expression> actualArgs) {
        super(functionName, Type.bool(), actualArgs);
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        PMLPatternFunction function = ctx.scope().getPatternFunction(getFunctionName());

        List<Value> values = new ArrayList<>();
        for (Expression e : getActualArgs()) {
            Value value = e.execute(ctx, pap);

            values.add(value);
        }

        return function
                .withCtx(ctx)
                .withOperands(values)
                .execute(pap);
    }
}
