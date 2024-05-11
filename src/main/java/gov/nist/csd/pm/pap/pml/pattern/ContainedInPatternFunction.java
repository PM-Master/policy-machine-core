package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.pml.expression.literal.ArrayLiteral;
import gov.nist.csd.pm.pap.pml.expression.literal.StringLiteral;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public class ContainedInPatternFunction extends PMLPatternFunctionStmt {

    public static Pattern pContainedIn(String varName, List<String> values) throws PMException {
        List<Value> args = new ArrayList<>();
        for (String s : values) {
            args.add(new StringValue(s));
        }

        return new ContainedInPatternFunction()
                .getPattern(varName, List.of(new ArrayValue(args, Type.string())));
    }

    public ContainedInPatternFunction() {
        super("pContainedIn", List.of(
                new PMLPatternArg("list", Type.array(Type.string()))
        ));
    }

    @Override
    public PMLPattern getPattern(String varName, List<Value> argValues) throws PMException {
        return new PMLPattern.Simple(varName, argValues, getSignature()) {
            @Override
            public boolean matches(Object value, PAP pap) throws PMException {
                List<Value> list = argValues.get(0).getArrayValue();
                Value toMatch = Value.fromObject(value);

                return list.contains(toMatch);
            }

            @Override
            public PatternExpression toPatternExpression() {
                ContainedInPatternFunction pFunc = new ContainedInPatternFunction();
                FunctionSignature signature = pFunc.getSignature();

                ArrayLiteral arrayLiteral = new ArrayLiteral(Type.string());
                for (Value value : argValues.get(0).getArrayValue()) {
                    arrayLiteral.add(new StringLiteral(value.getStringValue()));
                }

                return new PatternExpression(
                        varName,
                        new PatternFunctionInvokeExpression(signature.getFunctionName(), signature.getReturnType(), List.of(arrayLiteral))
                );
            }
        };
    }
}