package gov.nist.csd.pm.pap.pml.function.builtin;

import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.List;

public class Contains extends FunctionDefinitionStatement {

    public Contains() {
        super(new FunctionDefinitionStatement.Builder("contains")
                      .returns(Type.bool())
                      .args(
                              new FormalArgument("arr", Type.array(Type.any())),
                              new FormalArgument("element", Type.any())
                      )
                      .executor((ctx, pap) -> {
                          List<Value> valueArr = ctx.scope().getVariable("arr").getArrayValue();
                          Value element = ctx.scope().getVariable("element");
                          boolean contains = valueArr.contains(element);
                          return new BoolValue(contains);
                      })
                      .build()
        );
    }

}

