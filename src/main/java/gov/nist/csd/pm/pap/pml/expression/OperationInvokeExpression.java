package gov.nist.csd.pm.pap.pml.expression;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.PolicyPoint;
import gov.nist.csd.pm.pap.pml.PMLExecutor;
import gov.nist.csd.pm.pap.pml.context.ExecutionContext;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class OperationInvokeExpression extends FunctionInvokeExpression{
    @Override
    public Value execute(ExecutionContext ctx, PAP pap) throws PMException {
        ExecutionContext copy = ctx.copy().withPMLExecutor(new PMLExecutor());

        return super.execute(copy, pap);
    }

    public OperationInvokeExpression(String functionName, Type result,
                                     List<Expression> actualArgs) {
        super(functionName, result, actualArgs);


    }
}
