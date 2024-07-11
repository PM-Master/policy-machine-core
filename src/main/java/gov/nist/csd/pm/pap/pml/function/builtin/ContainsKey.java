package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;
import gov.nist.csd.pm.pap.pml.value.Value;

import java.util.Map;

public class ContainsKey extends FunctionDefinitionStatement {

    public ContainsKey() {
        super(new FunctionDefinitionStatement.Builder("containsKey")
                      .returns(Type.bool())
                      .args(
                              new FormalArg("map", Type.map(Type.any(), Type.any()), reqCap),
                              new FormalArg("key", Type.any(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          Map<Value, Value> valueMap = ctx.scope().getVariable("map").getMapValue();
                          Value element = ctx.scope().getVariable("key");
                          boolean contains = valueMap.containsKey(element);
                          return new BoolValue(contains);
                      })
                      .build()
        );
    }

}
