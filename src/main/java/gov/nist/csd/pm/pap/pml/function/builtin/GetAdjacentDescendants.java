package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
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
                              new FormalArg("nodeName", Type.string(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          Collection<String> descendants = query.graph().getAdjacentDescendants(ctx.scope().getVariable("nodeName").getStringValue());
                          List<Value> descValues = new ArrayList<>(descendants.size());

                          descendants.forEach(descValue -> descValues.add(new StringValue(descValue)));

                          return new ArrayValue(descValues, returnType);
                      })
                      .build()
        );
    }

}
