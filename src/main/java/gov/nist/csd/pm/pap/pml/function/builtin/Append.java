package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class Append extends FunctionDefinitionStatement {
    public Append() {
        super(new FunctionDefinitionStatement.Builder("append")
                      .returns(Type.array(Type.any()))
                      .args(
                              new FormalArgument("dst", Type.array(Type.any())),
                              new FormalArgument("src", Type.any())
                      )
                      .executor((ctx, pap) -> {
                          List<Value> valueArr = ctx.scope().getVariable("dst").getArrayValue();
                          Value srcValue = ctx.scope().getVariable("src");

                          valueArr.add(srcValue);

                          return new ArrayValue(valueArr, Type.array(Type.any()));
                      })
                      .build()
        );
    }
}
