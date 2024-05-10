package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class AppendAll extends FunctionDefinitionStatement {
    public AppendAll() {
        super(new FunctionDefinitionStatement.Builder("appendAll")
                      .returns(Type.array(Type.any()))
                      .args(
                              new FormalArgument("dst", Type.array(Type.any())),
                              new FormalArgument("src", Type.array(Type.any()))
                      )
                      .executor((ctx, pap) -> {
                          List<Value> dstValueArr = ctx.scope().getVariable("dst").getArrayValue();
                          List<Value> srcValueArr = ctx.scope().getVariable("src").getArrayValue();

                          dstValueArr.addAll(srcValueArr);

                          return new ArrayValue(dstValueArr, Type.array(Type.any()));
                      })
                      .build()
        );
    }
}
