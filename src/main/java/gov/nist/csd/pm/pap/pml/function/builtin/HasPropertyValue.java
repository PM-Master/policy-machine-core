package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;


public class HasPropertyValue extends FunctionDefinitionStatement {

    public HasPropertyValue() {
        super(new FunctionDefinitionStatement.Builder("hasPropertyValue")
                      .returns(Type.bool())
                      .args(
                              new FormalArg("nodeName", Type.string(), reqCap),
                              new FormalArg("key", Type.string(), reqCap),
                              new FormalArg("value", Type.string(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          String nodeName = ctx.scope().getVariable("nodeName").getStringValue();
                          String key = ctx.scope().getVariable("key").getStringValue();
                          String value = ctx.scope().getVariable("value").getStringValue();
                          Node node = query.graph().getNode(nodeName);
                          boolean has = node.getProperties().containsKey(key);
                          if (!has) {
                              return new BoolValue(false);
                          }

                          has = node.getProperties().get(key).equals(value);
                          return new BoolValue(has);
                      })
                      .build()
        );
    }

}
