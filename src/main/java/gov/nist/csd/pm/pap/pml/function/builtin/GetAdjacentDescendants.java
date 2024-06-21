package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetAdjacentDescendants extends FunctionDefinitionStatement {

    private static final Type returnType = Type.array(Type.string());

    public GetAdjacentDescendants() {
        super(new FunctionDefinitionStatement.Builder("getAdjacentDescendants")
                      .returns(returnType)
                      .args(
                              new FormalArgument("nodeName", Type.string())
                      )
                      .executor((ctx, pap) -> {
                          Collection<String> descendants = pap.query().graph().getAdjacentDescendants(ctx.scope().getVariable("nodeName").getStringValue());
                          List<Value> descValues = new ArrayList<>(descendants.size());

                          descendants.forEach(descValue -> descValues.add(new StringValue(descValue)));

                          return new ArrayValue(descValues, returnType);
                      })
                      .build()
        );
    }

}
