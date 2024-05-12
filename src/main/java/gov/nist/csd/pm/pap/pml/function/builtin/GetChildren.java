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

public class GetChildren extends FunctionDefinitionStatement {

    private static final Type returnType = Type.array(Type.string());

    public GetChildren() {
        super(new FunctionDefinitionStatement.Builder("getChildren")
                      .returns(returnType)
                      .args(
                              new FormalArgument("nodeName", Type.string())
                      )
                      .executor((ctx, pap) -> {
                          Collection<String> children = pap.query().graph().getChildren(ctx.scope().getVariable("nodeName").getStringValue());
                          List<Value> childValues = new ArrayList<>(children.size());

                          children.forEach(child -> childValues.add(new StringValue(child)));

                          return new ArrayValue(childValues, returnType);
                      })
                      .build()
        );
    }

}
