package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.ArrayValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class AppendAll extends FunctionDefinitionStatement {
    public AppendAll() {
        super(new FunctionDefinitionStatement.Builder("appendAll")
                      .returns(Type.array(Type.any()))
                      .args(
                              new FormalArg("dst", Type.array(Type.any()), reqCap),
                              new FormalArg("src", Type.array(Type.any()), reqCap)
                      )
                      .executor((ctx, query) -> {
                          List<Value> dstValueArr = ctx.scope().getVariable("dst").getArrayValue();
                          List<Value> srcValueArr = ctx.scope().getVariable("src").getArrayValue();

                          dstValueArr.addAll(srcValueArr);

                          return new ArrayValue(dstValueArr, Type.array(Type.any()));
                      })
                      .build()
        );
    }
}
