package gov.nist.csd.pm.pap.pml.function.builtin;

import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class Contains extends FunctionDefinitionStatement {

    public Contains() {
        super(new FunctionDefinitionStatement.Builder("contains")
                      .returns(Type.bool())
                      .args(
                              new FormalArg("arr", Type.array(Type.any()), reqCap),
                              new FormalArg("element", Type.any(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          List<Value> valueArr = ctx.scope().getVariable("arr").getArrayValue();
                          Value element = ctx.scope().getVariable("element");
                          boolean contains = valueArr.contains(element);
                          return new BoolValue(contains);
                      })
                      .build()
        );
    }

}

