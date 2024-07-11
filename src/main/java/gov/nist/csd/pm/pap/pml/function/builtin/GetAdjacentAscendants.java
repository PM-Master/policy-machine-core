package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.executable.PMLOperation;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GetAdjacentAscendants extends PMLOperation { // TODO IS AN OPERATION

    private static final Type returnType = Type.array(Type.string());

    public GetAdjacentAscendants() {
        super(new FunctionDefinitionStatement.Builder("getAdjacentAscendants")
                      .returns(returnType)
                      .args(
                              new FormalArg("nodeName", Type.string(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          Collection<String> ascendants = query.graph().getAdjacentAscendants(ctx.scope().getVariable("nodeName").getStringValue());
                          List<Value> ascValues = new ArrayList<>(ascendants.size());

                          ascendants.forEach(ascendant -> ascValues.add(new StringValue(ascendant)));

                          return new ArrayValue(ascValues, returnType);
                      })
                      .build()
        );
    }

}
