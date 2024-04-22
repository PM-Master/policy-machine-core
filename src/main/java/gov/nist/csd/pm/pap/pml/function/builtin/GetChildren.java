package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.ArrayList;
import java.util.List;

public class GetChildren extends FunctionDefinitionStatement {

    private static final Type returnType = Type.array(Type.string());

    public GetChildren() {
        super(new FunctionDefinitionStatement.Builder("getChildren")
                      .returns(returnType)
                      .args(
                              new FormalArgument("nodeName", Type.string())
                      )
                      .executor((ctx, author) -> {
                          List<String> children = author.graph().getChildren(ctx.scope().getVariable("nodeName").getStringValue());
                          List<Value> childValues = new ArrayList<>(children.size());
                          for (int i = 0; i < children.size(); i++) {
                              childValues.add(new StringValue(children.get(i)));
                          }

                          return new ArrayValue(childValues, returnType);
                      })
                      .build()
        );
    }

}
