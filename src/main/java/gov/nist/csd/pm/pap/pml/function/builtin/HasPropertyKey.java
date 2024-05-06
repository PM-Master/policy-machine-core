package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.pap.pml.function.FormalArgument;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.BoolValue;


public class HasPropertyKey extends FunctionDefinitionStatement {

    public HasPropertyKey() {
        super(new FunctionDefinitionStatement.Builder("hasPropertyKey")
                      .returns(Type.bool())
                      .args(
                              new FormalArgument("nodeName", Type.string()),
                              new FormalArgument("key", Type.string())
                      )
                      .executor((ctx, author) -> {
                          String nodeName = ctx.scope().getVariable("nodeName").getStringValue();
                          String key = ctx.scope().getVariable("key").getStringValue();
                          Node node = author.graph().getNode(nodeName);
                          boolean hasPropertyKey = node.getProperties().containsKey(key);
                          return new BoolValue(hasPropertyKey);
                      })
                      .build()
        );
    }

}
