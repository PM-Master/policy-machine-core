package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.obligation.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class ContainsPatternFunction extends PMLPatternFunction {

    public static Pattern pContains(String varName, String value) throws PMException {
        return new ContainsPatternFunction()
                .getPattern(varName, List.of(new StringValue(value)));
    }

    public ContainsPatternFunction() {
        super("pContains", List.of(
                new PMLPatternArg("o", Type.string())
        ));
    }

    @Override
    public PMLPattern getPattern(String varName, List<Value> argValues) throws PMException {
        return new PMLPattern.Simple(varName, argValues, getSignature()) {
            @Override
            public boolean matches(Object value, PAP pap) throws PMException {
                List<Value> list = Value.fromObject(value).getArrayValue();

                return list.contains(argValues.get(0));
            }

            @Override
            public PatternExpression toPatternExpression() {
                ContainsPatternFunction pFunc = new ContainsPatternFunction();
                FunctionSignature signature = pFunc.getSignature();

                return new PatternExpression(
                        varName,
                        new PatternFunctionExpression(signature.getFunctionName(), signature.getReturnType(), List.of(
                                new StringLiteral(argValues.get(0).getStringValue())
                        ))
                );
            }
        };
    }
}