package gov.nist.csd.pm.pap.pml.pattern2;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.exception.PMLExecutionException;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.FunctionInvokeExpression;
import gov.nist.csd.pm.pap.pml.function.PMLFunction;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternFunctionInvokeExpression extends FunctionInvokeExpression {

    public PatternFunctionInvokeExpression(PMLFunction function, List<Expression> actualArgs) {
        super(function, actualArgs);
    }

    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        PMLFunction func = getFunction();
        String funcName = func.getName();
        Map<String, PMLRequiredCapability> capMap = func.getPMLCapMap();
        ExecutionContext funcInvokeExecCtx = ctx.copy();
        Map<String, Object> operands = new HashMap<>();
        List<Expression> actualArgs = getActualArgs();

        for (int i = 0; i < actualArgs.size(); i++) {
            Expression argExpr = actualArgs.get(i);
            Value argValue = funcInvokeExecCtx.executeStatement(pap, argExpr);
            Map.Entry<String, PMLRequiredCapability> cap = getReqCapAtIndex(i, capMap);

            if (cap == null) {
                throw new PMLExecutionException("arg index " + i + " out of bounds for function " + funcName);
            } else if (!argValue.getType().equals(cap.getValue().type())) {
                throw new PMLExecutionException("expected " + cap.getValue().type() + " for arg " + i + " for function \""
                                                        + funcName + "\", got " + argValue.getType());
            }

            operands.put(cap.getKey(), argValue);
        }

        operands.put("value", ctx.scope().getVariable("value"));

        return func.withOperands(operands)
                   .execute(pap);
    }
}
