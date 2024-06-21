package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class AscendantOfPatternFunction extends PMLPatternFunctionStmt {

    public static Pattern pAscendantOf(String varName, String value) throws PMException {
        return new AscendantOfPatternFunction()
                .getPattern(varName, List.of(new StringValue(value)));
    }

    public AscendantOfPatternFunction() {
        super("pAscendantOf", List.of(
                new PMLPatternArg("node", Type.string()))
        );
    }

    @Override
    public PMLPattern getPattern(String varName, List<Value> argValues) throws PMException {
        return new PMLPattern.Simple(varName, argValues, getSignature()) {
            @Override
            public boolean matches(Object value, PAP pap) throws PMException {
                String node = argValues.get(0).getStringValue();
                String toMatch = Value.fromObject(value).getStringValue();

                return pap.query().graph().isAscendant(node, toMatch);
            }

            @Override
            public PatternExpression toPatternExpression() {
                AscendantOfPatternFunction pFunc = new AscendantOfPatternFunction();
                FunctionSignature signature = pFunc.getSignature();

                return new PatternExpression(
                        varName,
                        new PatternFunctionInvokeExpression(signature.getFunctionName(), signature.getReturnType(), List.of(
                                new StringLiteral(argValues.get(0).getStringValue())
                        ))
                );
            }
        };
    }
}
