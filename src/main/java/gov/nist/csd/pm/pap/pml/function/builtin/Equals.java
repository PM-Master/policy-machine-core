package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;
import gov.nist.csd.pm.pap.pml.value.Value;


public class Equals extends FunctionDefinitionStatement {

    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";

    public Equals() {
        super(new FunctionDefinitionStatement.Builder("equals")
                      .returns(Type.bool())
                      .args(
                              new FormalArg(VALUE1, Type.any(), reqCap),
                              new FormalArg(VALUE2, Type.any(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          Value v1 = ctx.scope().getVariable(VALUE1);
                          Value v2 = ctx.scope().getVariable(VALUE2);

                          return new BoolValue(v1.equals(v2));
                      })
                      .build()
        );
    }
}
