package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.obligation.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Collection;
import java.util.List;

public class DescendantOfPatternFunction extends PMLPatternFunction {

    public static Pattern pDescendantOf(String varName, String value) throws PMException {
        return new DescendantOfPatternFunction()
                .getPattern(varName, List.of(new StringValue(value)));
    }

    public DescendantOfPatternFunction() {
        super("pDescendantOf", List.of(
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


                Collection<String> attrs = pap.query().graph().getAttributeDescendants(node);
                Collection<String> pcs = pap.query().graph().getPolicyClassDescendants(node);

                return attrs.contains(toMatch) || pcs.contains(toMatch);
            }

            @Override
            public PatternExpression toPatternExpression() {
                DescendantOfPatternFunction pFunc = new DescendantOfPatternFunction();
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
