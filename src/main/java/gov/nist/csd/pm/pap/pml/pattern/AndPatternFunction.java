package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.common.obligation.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.PatternValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public class AndPatternFunction extends PMLPatternFunction {

    public static Pattern pAnd(String varName, Pattern ... patterns) throws PMException {
        List<Value> patternValues = new ArrayList<>();
        for (Pattern pattern : patterns) {
            patternValues.add(new PatternValue(pattern));
        }

        return new AndPatternFunction()
                .getPattern(varName, patternValues);
    }

    public AndPatternFunction() {
        super("pAnd", List.of(
                new PMLPatternReqCap("operands", Type.array(Type.pattern()))
        ));
    }

    @Override
    public PMLPattern getPattern(String varName, List<Value> argValues) throws PMException {
        return null;
    }

    @Override
    public PatternValue execute(PAP pap) throws PMException {
        new PMLPattern.Aggregatge(varName, argValues, getCapMap()) {
            @Override
            public boolean matches(Object value, PAP pap) throws PMException {
                for (Value argValue : getPatternValues()) {
                    Pattern patternValue = argValue.getPatternValue();
                    if (!patternValue.matches(value, pap)) {
                        return false;
                    }
                }

                return true;
            }

            @Override
            public PatternExpression toPatternExpression() {
                AndPatternFunction pFunc = new AndPatternFunction();

                List<Expression> actualArgs = new ArrayList<>();
                for (Value arg : argValues) {
                    Pattern patternValue = arg.getPatternValue();
                    actualArgs.add(patternValue.toPatternExpression());
                }

                return new PatternExpression(
                        varName,
                        new PatternFunctionExpression(pFunc.getOpName(), pFunc.getReturnType(), actualArgs)
                );
            }
        };



        return new PatternValue();
    }
}
