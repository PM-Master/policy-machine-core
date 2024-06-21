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

public class GetAdjacentAscendants extends FunctionDefinitionStatement {

    private static final Type returnType = Type.array(Type.string());

    public GetAdjacentAscendants() {
        super(new FunctionDefinitionStatement.Builder("getAdjacentAscendants")
                      .returns(returnType)
                      .args(
                              new FormalArgument("nodeName", Type.string())
                      )
                      .executor((ctx, pap) -> {
                          Collection<String> ascendants = pap.query().graph().getAdjacentAscendants(ctx.scope().getVariable("nodeName").getStringValue());
                          List<Value> ascValues = new ArrayList<>(ascendants.size());

                          ascendants.forEach(ascendant -> ascValues.add(new StringValue(ascendant)));

                          return new ArrayValue(ascValues, returnType);
                      })
                      .build()
        );
    }

}
