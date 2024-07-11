package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.obligation.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.value.PatternValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public class OrPatternFunction extends PMLPatternFunction {

    public static Pattern pOr(String varName, Pattern ... patterns) throws PMException {
        List<Value> patternValues = new ArrayList<>();
        for (Pattern pattern : patterns) {
            patternValues.add(new PatternValue(pattern));
        }

        return new OrPatternFunction()
                .getPattern(varName, patternValues);
    }

    public OrPatternFunction() {
        super("pOr", List.of());
    }

    @Override
    public PMLPattern getPattern(String varName, List<Value> argValues) throws PMException {
        return new PMLPattern.Aggregatge(varName, argValues, getSignature()) {
            @Override
            public boolean matches(Object value, PAP pap) throws PMException {
                for (Value argValue : getPatternValues()) {
                    Pattern patternValue = argValue.getPatternValue();
                    if (!patternValue.matches(value, pap)) {
                        return true;
                    }
                }

                return false;
            }

            @Override
            public PatternExpression toPatternExpression() {
                OrPatternFunction pFunc = new OrPatternFunction();
                FunctionSignature signature = pFunc.getSignature();

                List<Expression> actualArgs = new ArrayList<>();
                for (Value arg : argValues) {
                    Pattern patternValue = arg.getPatternValue();
                    actualArgs.add(patternValue.toPatternExpression());
                }

                return new PatternExpression(
                        varName,
                        new PatternFunctionExpression(signature.getFunctionName(), signature.getReturnType(), actualArgs)
                );
            }
        };
    }
}
