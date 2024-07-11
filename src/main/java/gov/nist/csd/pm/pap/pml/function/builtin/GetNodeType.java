package gov.nist.csd.pm.pap.pml.function.builtin;


import gov.nist.csd.pm.common.graph.node.Node;
import gov.nist.csd.pm.pap.pml.function.FormalArg;
import gov.nist.csd.pm.pap.pml.statement.operation.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import gov.nist.csd.pm.pap.pml.value.StringValue;

public class GetNodeType extends FunctionDefinitionStatement {

    public GetNodeType() {
        super(new FunctionDefinitionStatement.Builder("getNodeType")
                      .returns(Type.string())
                      .args(
                              new FormalArg("nodeName", Type.string(), reqCap)
                      )
                      .executor((ctx, query) -> {
                          Node node = query.graph().getNode(ctx.scope().getVariable("nodeName").getStringValue());
                          return new StringValue(node.getType().toString());
                      })
                      .build()
        );
    }

}

