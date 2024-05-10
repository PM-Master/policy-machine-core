package gov.nist.csd.pm.pap.pml.pattern;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.PAP;
import gov.nist.csd.pm.pap.op.pattern.Pattern;
import gov.nist.csd.pm.pap.op.pattern.ReferencedPolicyEntities;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public class AnyPatternFunction extends PMLPatternFunctionStmt {

    public static Pattern pAny(String varName) throws PMException {
        return new AnyPatternFunction()
                .getPattern(varName, List.of());
    }

    public AnyPatternFunction() {
        super("pAny", List.of());
    }

    @Override
    public PMLPattern getPattern(String varName, List<Value> argValues) throws PMException {
        return new PMLPattern.Simple(varName, argValues, getSignature()) {
            @Override
            public boolean matches(Object value, PAP pap) throws PMException {
                return true;
            }

            @Override
            public ReferencedPolicyEntities getReferencedPolicyEntities() {
                return new ReferencedPolicyEntities(true);
            }

            @Override
            public PatternExpression toPatternExpression() {
                AnyPatternFunction pFunc = new AnyPatternFunction();
                FunctionSignature signature = pFunc.getSignature();
                return new PatternExpression(
                        varName,
                        new PatternFunctionInvokeExpression(signature.getFunctionName(), signature.getReturnType(), new ArrayList<>())
                );
            }
        };
    }
}
