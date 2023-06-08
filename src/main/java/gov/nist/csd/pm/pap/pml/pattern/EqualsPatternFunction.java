package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class EqualsPatternFunction extends PMLPatternFunctionStmt {

    public static Pattern pEquals(String varName, Value value) throws PMException {
        return new EqualsPatternFunction()
                .getPattern(varName, List.of(value));
    }

    public EqualsPatternFunction() {
        super("pEquals", List.of(new PMLPatternArg("a", Type.any())));
    }

    @Override
    public PMLPattern getPattern(String varName, List<Value> argValues) throws PMException {

        return new PMLPattern.Simple(varName, argValues, getSignature()) {
            @Override
            public boolean matches(Object value, PAP pap) {
                Value toMatch = Value.fromObject(value);
                return argValues.get(0).equals(toMatch);
            }

            @Override
            public PatternExpression toPatternExpression() {
                EqualsPatternFunction pFunc = new EqualsPatternFunction();
                FunctionSignature signature = pFunc.getSignature();

                Value argValue = argValues.get(0);
                Expression argExpr;
                if (argValue.getType().isString()) {
                    argExpr = new StringLiteral(argValue.getStringValue());
                } else {
                    ArrayLiteral arr = new ArrayLiteral(Type.string());
                    for (Value value : argValues.get(0).getArrayValue()) {
                        arr.add(new StringLiteral(value.getStringValue()));
                    }

                    argExpr = arr;
                }

                return new PatternExpression(
                        varName,
                        new PatternFunctionInvokeExpression(signature.getFunctionName(), signature.getReturnType(), List.of(argExpr))
                );
            }
        };
    }
}
