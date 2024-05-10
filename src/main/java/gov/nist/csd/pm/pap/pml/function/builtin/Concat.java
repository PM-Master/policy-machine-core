package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class Concat extends FunctionDefinitionStatement {

    private static final String ARR_ARG = "arr";

    public Concat() {
        super(new FunctionDefinitionStatement.Builder("concat")
                      .returns(Type.string())
                      .args(
                              new FormalArgument(ARR_ARG, Type.array(Type.string()))
                      )
                      .executor((ctx, pap) -> {
                          List<Value> arr = ctx.scope().getVariable(ARR_ARG).getArrayValue();
                          StringBuilder s = new StringBuilder();
                          for (Value v : arr) {
                              s.append(v.getStringValue());
                          }

                          return new StringValue(s.toString());
                      })
                      .build()
        );
    }
}
